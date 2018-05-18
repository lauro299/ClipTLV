package com.clip.tlv

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.ListAdapter
import com.clip.tlv.databinding.ActivityMainBinding
import com.clip.tlv.model.TLVDataConverter
import com.clip.tlv.model.Tag
import com.github.nitrico.lastadapter.LastAdapter
import com.jakewharton.rxbinding2.widget.RxTextView

class MainActivity : AppCompatActivity() {

    var tlvDecoder = TLVDataConverter()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding = DataBindingUtil.setContentView<ActivityMainBinding>(this,R.layout.activity_main)
        binding.resultTags.layoutManager = LinearLayoutManager(this)
        RxTextView.textChanges(binding.textDecode)
                .subscribe({
                    LastAdapter( tlvDecoder.decode(it.toString()),BR.item )
                            .map<Tag>(R.layout.fragment_list_tag)
                            .into(binding.resultTags)
                })
    }
}
