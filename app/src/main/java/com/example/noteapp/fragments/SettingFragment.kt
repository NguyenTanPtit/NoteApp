package com.example.noteapp.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.noteapp.databinding.AlertDialogLoginBinding
import com.example.noteapp.databinding.FragmentSettingBinding
import com.example.noteapp.repository.FirebaseRepository
import com.example.noteapp.repository.ResponseState
import com.example.noteapp.utils.loadImage
import com.example.noteapp.viewModel.FirebaseViewModel
import com.example.noteapp.viewModel.FirebaseViewModelFactory
import com.example.noteapp.viewModel.NoteActivityViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.transition.MaterialElevationScale
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingFragment : Fragment(R.layout.fragment_setting) {

    private var _binding: FragmentSettingBinding? = null
    private val binding : FragmentSettingBinding
        get() = _binding!!
    private lateinit var navController: NavController
    private var user: FirebaseUser? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    val RC_SIGN_IN = 0
    private val firebaseViewModel: FirebaseViewModel by viewModels{FirebaseViewModelFactory(repo = FirebaseRepository())}
    private val noteActivityViewModel:NoteActivityViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialElevationScale(false).apply {
            duration = 350
        }
        enterTransition = MaterialElevationScale(true).apply {
            duration = 350
        }
    }
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

    @SuppressLint("SetTextI18n")
    private fun initView(){
        user = Firebase.auth.currentUser
        initGoogleSignInClient()
        binding.progress.visibility = View.GONE
        if (user!=null){
            binding.apply {
                btnGoogle.visibility = View.GONE
                logoutParent.visibility = View.VISIBLE
                avatarParent.visibility = View.VISIBLE
                Glide.with(requireContext()).load(user!!.photoUrl).into(avatarImg)
                textViewAvatar.text = "Hello, ${user!!.displayName}"
            }
        }else{
            binding.apply {
                avatarParent.visibility = View.GONE
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
                getGoogleAuthCredential(account)

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

        firebaseViewModel.signInWithGoogle(googleAuthCredential)
        firebaseViewModel.authenticateUserLiveData.observe(viewLifecycleOwner) { authenticatedUser ->
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
                        user = Firebase.auth.currentUser
                        binding.apply {
                            btnGoogle.visibility = View.GONE
                            logoutParent.visibility = View.VISIBLE
                            avatarParent.visibility = View.VISIBLE
                            Glide.with(requireContext()).load(user!!.photoUrl).into(avatarImg)
                            textViewAvatar.text = "Hello, ${user!!.displayName}"
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
            GlobalScope.launch(Dispatchers.Main) {
                if (user == null){
                    showDialogLogin()
                }else {
                    Glide.with(requireContext()).asGif().load(R.raw.icon_sync).into(binding.syncBtn)

                    firebaseViewModel.syncNoteAndRemind(user!!.uid,requireContext(),noteActivityViewModel)
                    delay(2000)
                    binding.syncBtn.loadImage(R.drawable.icon_sync)
                }
            }

        }

        binding.btnGoogle.setOnClickListener {
            signInUsingGoogle()
        }

        binding.logoutImg.setOnClickListener {
            Firebase.auth.signOut()
            googleSignInClient.signOut()
            user = Firebase.auth.currentUser
            binding.apply {
                btnGoogle.visibility = View.VISIBLE
                logoutParent.visibility = View.GONE
                syncBtn.loadImage(R.drawable.icon_sync)
                avatarParent.visibility = View.GONE
            }
        }
    }

    private fun showDialogLogin(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(context)
        builder.setCancelable(true)
        val inflater = LayoutInflater.from(context)
        val viewDialogBinding = AlertDialogLoginBinding.inflate(inflater)

        builder.setView(viewDialogBinding.root)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
        alertDialog.window?.setBackgroundDrawableResource(R.drawable.bg_alert_dialog)

        viewDialogBinding.alertDialogOK.setOnClickListener {
            alertDialog.cancel()
        }
    }

}