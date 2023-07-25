package com.example.noteapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.noteapp.R
import com.example.noteapp.databinding.FragmentSettingBinding
import com.example.noteapp.repository.AuthRepository
import com.example.noteapp.repository.ResponseState
import com.example.noteapp.viewModel.LoginWithGGViewModel
import com.example.noteapp.viewModel.LoginWithGGViewModelFactory
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class SettingFragment : Fragment(R.layout.fragment_setting) {

    private var _binding: FragmentSettingBinding? = null
    private val binding : FragmentSettingBinding
        get() = _binding!!
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 0
    private val loginViewModel: LoginWithGGViewModel by viewModels{LoginWithGGViewModelFactory(repo = AuthRepository())}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initView()
        setOnClick()
    }

    private fun initView(){
        auth = Firebase.auth
        user = auth.currentUser
        initGoogleSignInClient()
        binding.progress.visibility = View.GONE
        if (user!=null){
            binding.apply {
                btnGoogle.visibility = View.GONE
                logoutParent.visibility = View.VISIBLE
            }
        }else{
            binding.apply {
                btnGoogle.visibility = View.VISIBLE
                logoutParent.visibility = View.GONE
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent ? ) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java) !!
                if (account != null) {
                    getGoogleAuthCredential(account)
                }

            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
            }
        }
    }
    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokeId = account.idToken
        val googleAuthCredential = GoogleAuthProvider.getCredential(googleTokeId, null)
        signInWithGoogleAuthCredential(googleAuthCredential)
    }

    private fun initGoogleSignInClient() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
    }
    private fun signInUsingGoogle() {
        val signInGoogleIntent = googleSignInClient.signInIntent
        startActivityForResult(signInGoogleIntent, RC_SIGN_IN)
    }
    private fun signInWithGoogleAuthCredential(googleAuthCredential: AuthCredential) {

        loginViewModel.signInWithGoogle(googleAuthCredential)
        loginViewModel.authenticateUserLiveData.observe(viewLifecycleOwner) { authenticatedUser ->
            when (authenticatedUser) {
                is ResponseState.Error -> {
                    binding.progress.visibility= View.GONE
                    authenticatedUser.message?.let {
                        Toast.makeText(requireContext(), "Fail to Login", Toast.LENGTH_SHORT).show()
                        Log.d("Login", it)
                    }
                }

                is ResponseState.Success -> {
                    binding.progress.visibility= View.GONE
                    if (authenticatedUser.data != null) {
                        binding.apply {
                            btnGoogle.visibility = View.GONE
                            logoutParent.visibility = View.VISIBLE
                        }
                    }
                }

                is ResponseState.Loading -> {
                    binding.progress.visibility= View.VISIBLE
                }
            }
        }

    }


    private fun setOnClick(){
        binding.backBtn.setOnClickListener {
            navController.popBackStack()
        }

        binding.syncBtn.setOnClickListener {
            if (user == null){
                //TODO show dialog login
                Toast.makeText(requireContext(), "Null User", Toast.LENGTH_SHORT).show()
            }else {
                Glide.with(requireContext()).asGif().load(R.raw.icon_sync).into(binding.syncBtn)
            }
        }

        binding.btnGoogle.setOnClickListener {
            signInUsingGoogle()
        }

        binding.logoutImg.setOnClickListener {
            auth.signOut()
        }
    }


}