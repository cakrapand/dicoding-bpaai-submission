package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.storyapp.R
import com.example.storyapp.data.StoryResult
import com.example.storyapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory.getInstance(requireActivity().dataStore)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
        playAnimation()
    }

    private fun moveToLogin(){
        val mLoginFragment = LoginFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.popBackStack()
        mFragmentManager.commit {
            replace(R.id.frame_container, mLoginFragment, LoginFragment::class.java.simpleName)
        }
    }

    private fun setupAction(){
        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString().trim()
            val email = binding.edRegisterEmail.text.toString().trim()
            val password = binding.edRegisterPassword.text.toString()
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8){
                binding.apply {
                    edRegisterName.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    edRegisterEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    edRegisterPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                }
                authViewModel.register(name, email, password).observe(requireActivity()){ result ->
                    when(result){
                        is StoryResult.Loading ->{
                            binding.progrerssBarRegister.visibility = View.VISIBLE
                        }
                        is StoryResult.Success ->{
                            binding.progrerssBarRegister.visibility = View.GONE
                            moveToLogin()
                            Toast.makeText(requireActivity(), getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        }
                        is StoryResult.Error ->{
                            binding.progrerssBarRegister.visibility = View.GONE
                            Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                Toast.makeText(requireActivity(), getString(R.string.auth_invalid), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnMoveLogin.setOnClickListener { moveToLogin() }
    }

    private fun playAnimation(){
        val tvRegister = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(500)
        val edRegisterName = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(500)
        val edRegisterEmail = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(500)
        val edRegisterPassword = ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(500)
        val btnMoveLogin = ObjectAnimator.ofFloat(binding.btnMoveLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(btnMoveLogin, btnRegister)
        }

        AnimatorSet().apply {
            playSequentially(tvRegister, edRegisterName, edRegisterEmail, edRegisterPassword, together)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}