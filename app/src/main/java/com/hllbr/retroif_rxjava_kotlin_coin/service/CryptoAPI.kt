package com.hllbr.retroif_rxjava_kotlin_coin.service

import com.hllbr.retroif_rxjava_kotlin_coin.model.CryptoModel
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.http.GET

interface CryptoAPI {
/*
Burada bir obje oluşturmak yerine burada yaptığımz işlemleri diğer taraflarda kullanabiliyorduk farklı obje odaklama özelliklerinden de faydalanarak
Burada hangi methodları kullanacağımı belirtmem gerekiyor.
***GET,POST,UPDATE,DELETE
GENELDE VERİYİ ÇEKMEK İÇİN GET
VERİYİ YAZMAK DEĞĞİŞTİRMEK İÇİN POST
SİLMEK İÇİN DELETE
GÜNCELLEMEK İÇİN UPDATE GİBİ METHODLARI KULLANABİLİYORUZ.
BU İŞLEM İÇİN BİZE GET GEREKİYOR.

*/
    @GET("prices?key=a82399be10cc5c1dadff681c8df1eeefc123916b")
        fun getData():Observable<List<CryptoModel>>
  //  fun getData():retrofit2.Call<List<CryptoModel>>
}