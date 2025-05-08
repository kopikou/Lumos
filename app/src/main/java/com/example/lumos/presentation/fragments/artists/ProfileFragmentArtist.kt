package com.example.lumos.presentation.fragments.artists
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.lumos.R
import com.example.lumos.domain.entities.Artist
import com.google.android.material.button.MaterialButton

class ProfileFragmentArtist(private val artist: Artist) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_artists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Находим все TextView
        val tvArtistName = view.findViewById<TextView>(R.id.tvArtistName)
        val tvPhone = view.findViewById<TextView>(R.id.tvPhone)
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        val btnEditProfile = view.findViewById<MaterialButton>(R.id.btnEditProfile)

        // Устанавливаем данные артиста
        tvArtistName.text = "${artist.firstName} ${artist.lastName}"
        tvPhone.text = "+ ${artist.phone}"
        tvBalance.text = "${artist.balance} ₽"

        // Обработчик кнопки редактирования
        btnEditProfile.setOnClickListener {
            // Здесь можно добавить логику для редактирования профиля
        }
    }
}