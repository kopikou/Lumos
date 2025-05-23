package com.example.lumos.presentation.views.fragments.managers

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lumos.presentation.views.activities.LoginActivity
import com.example.lumos.data.local.auth.TokenManager
import com.example.lumos.data.remote.impl.EarningServiceImpl
import com.example.lumos.data.remote.impl.OrderServiceImpl
import com.example.lumos.data.repository.EarningRepositoryImpl
import com.example.lumos.data.repository.OrderRepositoryImpl
import com.example.lumos.databinding.FragmentProfileManagersBinding
import com.example.lumos.domain.usecases.GetFinancialDataUseCase
import com.example.lumos.domain.usecases.LogoutUseCase
import com.example.lumos.presentation.viewModels.managers.ProfileManagerViewModel
import com.example.lumos.presentation.viewModels.managers.ProfileManagerViewModelFactory
import java.text.DecimalFormat

class ProfileFragmentManager : Fragment() {
    private lateinit var binding: FragmentProfileManagersBinding
    private lateinit var viewModel: ProfileManagerViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileManagersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupObservers()
        setupListeners()

        viewModel.loadFinancialData()
    }

    private fun setupViewModel() {
        val tokenManager = TokenManager(requireContext())
        val orderRepository = OrderRepositoryImpl(OrderServiceImpl())
        val earningRepository = EarningRepositoryImpl(EarningServiceImpl())

        val getFinancialDataUseCase = GetFinancialDataUseCase(orderRepository, earningRepository)
        val logoutUseCase = LogoutUseCase(tokenManager)

        val factory = ProfileManagerViewModelFactory(
            getFinancialDataUseCase,
            logoutUseCase
        )

        viewModel = ViewModelProvider(this, factory)[ProfileManagerViewModel::class.java]
    }

    private fun setupObservers() {
        viewModel.financialData.observe(viewLifecycleOwner) { data ->
            val formatter = DecimalFormat("#,###.00 ₽")
            binding.tvIncome.text = formatter.format(data.income)
            binding.tvExpenses.text = formatter.format(data.expenses)
            binding.tvProfit.text = formatter.format(data.profit)
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error.isNotBlank()) {
                binding.tvIncome.text = "Ошибка"
                binding.tvExpenses.text = "Ошибка"
                binding.tvProfit.text = "Ошибка"
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupListeners() {
        binding.btnLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Выход")
            .setMessage("Вы уверены, что хотите выйти из профиля?")
            .setPositiveButton("Да") { _, _ ->
                viewModel.logout()
                navigateToLogin()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun navigateToLogin() {
        startActivity(Intent(requireActivity(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        } )
        requireActivity().finish()
    }
}