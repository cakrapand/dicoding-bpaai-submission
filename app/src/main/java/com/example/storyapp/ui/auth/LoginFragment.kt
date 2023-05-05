package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.util.Patterns
import android.view.*
import androidx.fragment.app.Fragment
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.data.StoryResult
import com.google.android.material.snackbar.Snackbar

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

    private val authViewModel: AuthViewModel by activityViewModels {
        AuthViewModelFactory.getInstance(requireActivity().dataStore)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAction()
        playAnimation()
    }

    private fun setupAction(){
        binding.edLoginEmail.isRegister = false
        binding.edLoginPassword.isRegister = false

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString()
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length >= 8){
                binding.apply {
                    edLoginEmail.onEditorAction(IME_ACTION_DONE)
                    edLoginPassword.onEditorAction(IME_ACTION_DONE)
                }
                authViewModel.login(email, password).observe(requireActivity()){result ->
                    when(result){
                        is StoryResult.Loading ->{
                            binding.progrerssBarLogin.visibility = View.VISIBLE
                        }
                        is StoryResult.Success ->{
                            binding.progrerssBarLogin.visibility = View.GONE
                            Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        }
                        is StoryResult.Error ->{
                            binding.progrerssBarLogin.visibility = View.GONE
                            Toast.makeText(requireActivity(), getString(R.string.login_wrong), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                Toast.makeText(requireActivity(), getString(R.string.auth_invalid), Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnMoveRegister.setOnClickListener {
            val mRegisterFragment = RegisterFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.commit {
                addToBackStack(null)
                replace(R.id.frame_container, mRegisterFragment, RegisterFragment::class.java.simpleName)
            }
        }
    }

    private fun playAnimation(){
        val tvLogin = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(500)
        val edLoginEmail = ObjectAnimator.ofFloat(binding.edLoginEmail, View.ALPHA, 1f).setDuration(500)
        val edLoginPassword = ObjectAnimator.ofFloat(binding.edLoginPassword, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnMoveRegister = ObjectAnimator.ofFloat(binding.btnMoveRegister, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(btnLogin, btnMoveRegister)
        }

        AnimatorSet().apply {
            playSequentially(tvLogin, edLoginEmail, edLoginPassword, together)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}