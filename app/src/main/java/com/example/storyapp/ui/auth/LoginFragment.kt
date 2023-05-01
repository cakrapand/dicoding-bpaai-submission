package com.example.storyapp.ui.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.data.Result
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

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            binding.apply {
                edLoginEmail.onEditorAction(IME_ACTION_DONE)
                edLoginPassword.onEditorAction(IME_ACTION_DONE)
            }
            authViewModel.login(email, password).observe(requireActivity()){result ->
                when(result){
                    is Result.Loading ->{
                        binding.progrerssBarLogin.visibility = View.VISIBLE
                    }
                    is Result.Success ->{
                        Toast.makeText(requireContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show()
                        binding.progrerssBarLogin.visibility = View.GONE
                    }
                    is Result.Error ->{
                        binding.progrerssBarLogin.visibility = View.GONE
                        Snackbar.make(binding.root, result.error, Snackbar.LENGTH_SHORT).show()
                    }
                }
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

        playAnimation()
    }

    private fun playAnimation(){
//        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
//            duration = 6000
//            repeatCount = ObjectAnimator.INFINITE
//            repeatMode = ObjectAnimator.REVERSE
//        }.start()

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