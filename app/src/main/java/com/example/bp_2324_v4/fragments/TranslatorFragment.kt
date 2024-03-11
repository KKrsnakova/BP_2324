package com.example.bp_2324_v4.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.bp_2324_v4.databinding.FragmentTranslatorBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions

class TranslatorFragment : Fragment() {

    private var _binding: FragmentTranslatorBinding? = null
    private val binding get() = _binding!!

    private lateinit var translatorOptions: TranslatorOptions
    private lateinit var translator: Translator

    private var sourceLanguageCode = "en"
    private var targetLanguageCode = "cs"
    private var sourceLanguageTitle = "English"
    private var targetLanguageTitle = "Czech"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTranslatorBinding.inflate(inflater, container, false)

        binding.apply {
            btnSourceLanguageChoose.text = sourceLanguageTitle
            btnTargetLanguageChoose.text = targetLanguageTitle

            btnSourceLanguageChoose.setOnClickListener {
                swapLanguages()
            }

            btnTargetLanguageChoose.setOnClickListener {
                swapLanguages()
            }

            btnTranslate.setOnClickListener {
                validateData()
            }
        }

        return binding.root
    }

    private fun validateData() {
        val sourceLanguageText = binding.etSource.text.toString().trim()
        if (sourceLanguageText.isEmpty()) {
            Toast.makeText(requireContext(), "Enter text to translate", Toast.LENGTH_SHORT).show()
        } else {
            startTranslation(sourceLanguageText)
        }
    }

    private fun startTranslation(sourceLanguageText: String) {
        val progressBar = ProgressBar(requireContext())
        progressBar.isIndeterminate = true
        progressBar.visibility = View.VISIBLE
        binding.progressBarLayout.addView(progressBar)

        translatorOptions = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguageCode)
            .setTargetLanguage(targetLanguageCode)
            .build()
        translator = Translation.getClient(translatorOptions)

        val downConditions = DownloadConditions.Builder()
            .requireWifi()
            .build()

        translator.downloadModelIfNeeded(downConditions)
            .addOnSuccessListener {
                translator.translate(sourceLanguageText)
                    .addOnSuccessListener { translatedText ->
                        progressBar.visibility = View.GONE
                        binding.tvTranslated.text = translatedText
                    }
                    .addOnFailureListener { e ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            requireContext(),
                            "Error translation: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
    }

    private fun swapLanguages() {
        val tempCode = sourceLanguageCode
        val tempTitle = sourceLanguageTitle
        sourceLanguageCode = targetLanguageCode
        sourceLanguageTitle = targetLanguageTitle
        targetLanguageCode = tempCode
        targetLanguageTitle = tempTitle

        binding.btnSourceLanguageChoose.text = sourceLanguageTitle
        binding.btnTargetLanguageChoose.text = targetLanguageTitle

        if (sourceLanguageCode == "en") {
            binding.etSource.hint = "Enter English text"
        } else {
            binding.etSource.hint = "Enter Czech text"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
