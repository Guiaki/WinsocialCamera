/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.example.cameraxbasic.ui.fragments

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.android.example.cameraxbasic.R
import com.android.example.cameraxbasic.ui.main.MainViewModel
import com.android.example.cameraxbasic.utils.CircleTransform
import com.android.example.cameraxbasic.utils.ViewModelEvent
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.fragment_gallery.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.util.*

val EXTENSION_WHITELIST = arrayOf("JPG")

class GalleryFragment internal constructor() : Fragment() {
    protected val mainViewModel: MainViewModel by viewModel(MainViewModel::class)
    private val args: GalleryFragmentArgs by navArgs()

    private lateinit var mediaList: MutableList<File>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        val rootDirectory = File(args.rootDirectory)
        mediaList = rootDirectory.listFiles { file ->
            EXTENSION_WHITELIST.contains(file.extension.toUpperCase(Locale.ROOT))
        }?.sortedDescending()?.toMutableList() ?: mutableListOf()
        mainViewModel.observeViewModelEvents().observe(this, Observer {
            val event = it.takeUnless { it == null || it.handled } ?: return@Observer
            handleViewModelAction(event)
        })
    }

    protected open fun handleViewModelAction(event: ViewModelEvent) {
        if(event.eventName == "imgur"){
            txv_imgur.text = "Link: ${event.eventValue}"
            txv_age.text = "Calculando idade e genero..."
            txv_age.visibility = VISIBLE
        }
        if(event.eventName == "age"){
            txv_age.text = "Idade: ${event.eventValue}"
            txv_age.visibility = VISIBLE
            loadingPanel.visibility = GONE
        }
        if(event.eventName == "gender"){
            txv_gender.text = "Genero: ${event.eventValue}"
            txv_gender.visibility = VISIBLE
            loadingPanel.visibility = GONE
        }
        event.handle(this)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_gallery, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val resource = mediaList[0].absolutePath ?: R.drawable.ic_photo
        Glide.with(view.context)
                .load(resource)
                .transform(CircleTransform(view.context))
                .dontAnimate()
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(view.findViewById<ImageButton>(R.id.imv_image))

        view.findViewById<ImageButton>(R.id.back_button).setOnClickListener {
            Navigation.findNavController(requireActivity(), R.id.fragment_container).navigateUp()
        }
        mainViewModel.uploadImageToImgur(mediaList[0].absolutePath);
    }
}
