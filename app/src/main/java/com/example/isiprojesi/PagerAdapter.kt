package com.example.isiprojesi

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.isiprojesi.fragmentler.OgrencilerFragment


class PagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 3
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0-> {return "Okul"}
            1-> {return "Öğrenciler"}
           2-> {return "Bilgilendirmeler"}
        }
        return super.getPageTitle(position)
    }

    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return OkulVerileriFragment()
            }
            1 -> {
                return OgrencilerFragment()
            }
            2 -> {
                return OkulVerileriFragment()
            }

            else -> {
                return OkulVerileriFragment()
            }
        }
    }


}
