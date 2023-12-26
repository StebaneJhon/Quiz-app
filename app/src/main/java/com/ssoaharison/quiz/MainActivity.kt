package com.ssoaharison.quiz

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.ThemeUtils
import androidx.appcompat.widget.ThemeUtils.getThemeAttrColor
import androidx.fragment.app.commit
import com.google.android.material.color.MaterialColors
import com.ssoaharison.quiz.databinding.ActivityMainBinding
import com.ssoaharison.quiz.quiz.QuizFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        window.statusBarColor = MaterialColors.getColor(this, com.google.android.material.R.attr.colorSurfaceContainerLowest, Color.BLACK)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add(R.id.fragment_container_view, QuizFragment())
            }
        }

    }

}