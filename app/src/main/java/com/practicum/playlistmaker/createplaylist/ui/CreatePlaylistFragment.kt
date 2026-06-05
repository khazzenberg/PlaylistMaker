package com.practicum.playlistmaker.createplaylist.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.createplaylist.presentation.CreatePlaylistViewModel
import com.practicum.playlistmaker.createplaylist.presentation.NameState
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream
import kotlin.getValue

import androidx.activity.OnBackPressedDispatcher
import com.bumptech.glide.load.resource.bitmap.CenterCrop

class CreatePlaylistFragment : Fragment() {
    private var _binding: FragmentCreatePlaylistBinding? = null
    private lateinit var simpleTextWatcher: TextWatcher
    private var fileP: File? = null
    private lateinit var confirmDialog: MaterialAlertDialogBuilder


    companion object {
        fun newInstance() = CreatePlaylistFragment()
    }

    private val viewModel: CreatePlaylistViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding?.pickerImage?.let {
            Glide.with(this)
                .load(R.drawable.ic_add_photo_312)
                .placeholder(R.drawable.ic_add_photo_312)
                .centerCrop()
                .transform(RoundedCorners(dpToPx(8f)))
                .into(
                    it
                )
        }

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    _binding?.pickerImage?.let {
                        Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.ic_add_photo_312)
                            .transform(
                                CenterCrop(), RoundedCorners(
                                    dpToPx(
                                        8f
                                    )
                                )
                            )
                            .into(
                                it
                            )
                    }
                    val uniqueName = "cover_${System.currentTimeMillis()}"
                    fileP = saveImageToPrivateStorage(uri, uniqueName)
                }
            }

        _binding?.pickerImage?.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        _binding?.menuButton?.setOnClickListener { handleBackNavigation() }
        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel?.changeName(s?.toString() ?: "")
            }

            override fun afterTextChanged(s: Editable?) {
            }
        }
        _binding?.nameEditText?.addTextChangedListener(simpleTextWatcher)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        _binding?.create?.setOnClickListener {
            viewModel.createPlaylist(
                _binding?.nameEditText?.text.toString(),
                _binding?.descriptionEditText?.text.toString(),
                fileP?.absolutePath?: ""
            )
            Toast.makeText(
                requireContext(),
                "Плейлист ${_binding?.nameEditText?.text.toString()} создан",
                Toast.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }

        confirmDialog = MaterialAlertDialogBuilder(requireContext(), R.style.NewMaterialDialog)
            .setTitle(getString(R.string.finish_create_pl))
            .setMessage(getString(R.string.finish_create_pl_msg))
            .setNeutralButton(getString(R.string.cancel)) { dialog, which ->
                dialog.dismiss()
            }.setPositiveButton(getString(R.string.finish)) { dialog, which ->
                findNavController().popBackStack()
            }

        requireActivity().onBackPressedDispatcher.addCallback(
            this,
            object : androidx.activity.OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackNavigation()
                }
            })

        _binding?.create?.isEnabled = false
    }
    fun handleBackNavigation(){
        if (_binding?.nameEditText?.text.toString().isNotEmpty()
            || (fileP?.absolutePath?.isNotEmpty() == true)
            || _binding?.descriptionEditText?.text.toString().isNotEmpty()
        )
            confirmDialog.show()
        else findNavController().popBackStack()
    }
    private fun render(state: NameState) {
        if (state.isEmpty) {
            _binding?.create?.isEnabled = false
        } else {
            _binding?.create?.isEnabled = true
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    private fun saveImageToPrivateStorage(uri: Uri, name: String): File {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath =
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "myalbum"
            )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "$name.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireActivity().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return file
    }
}