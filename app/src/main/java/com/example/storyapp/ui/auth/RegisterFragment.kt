package com.example.storyapp.ui.auth

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
import com.example.storyapp.data.Result
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.google.android.material.snackbar.Snackbar

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

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            if(Patterns.EMAIL_ADDRESS.matcher(email).matches() && password.length > 6){
                binding.apply {
                    edRegisterName.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    edRegisterEmail.onEditorAction(EditorInfo.IME_ACTION_DONE)
                    edRegisterPassword.onEditorAction(EditorInfo.IME_ACTION_DONE)
                }
                authViewModel.register(name, email, password).observe(requireActivity()){ result ->
                    when(result){
                        is Result.Loading ->{
                            binding.progrerssBarRegister.visibility = View.VISIBLE
                        }
                        is Result.Success ->{
                            binding.progrerssBarRegister.visibility = View.GONE
                            moveToLogin()
                            Toast.makeText(requireActivity(), getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                        }
                        is Result.Error ->{
                            binding.progrerssBarRegister.visibility = View.GONE
                            Toast.makeText(requireActivity(), result.error, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }else{
                Toast.makeText(requireActivity(), getString(R.string.register_invalid), Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnMoveLogin.setOnClickListener { moveToLogin()}
    }

    fun moveToLogin(){
        val mLoginFragment = LoginFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.popBackStack()
        mFragmentManager.commit {
            replace(R.id.frame_container, mLoginFragment, LoginFragment::class.java.simpleName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}