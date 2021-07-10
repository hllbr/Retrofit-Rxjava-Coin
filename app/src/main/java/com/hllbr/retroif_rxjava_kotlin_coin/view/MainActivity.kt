package com.hllbr.retroif_rxjava_kotlin_coin.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hllbr.retroif_rxjava_kotlin_coin.R
import com.hllbr.retroif_rxjava_kotlin_coin.adapter.RecyclerViewAdapter
import com.hllbr.retroif_rxjava_kotlin_coin.model.CryptoModel
import com.hllbr.retroif_rxjava_kotlin_coin.service.CryptoAPI
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(),RecyclerViewAdapter.Listener{
    private val BASE_URL = "https://api.nomics.com/v1/"
    private var cryptoModels:ArrayList<CryptoModel>? = null
    private var recyclerViewAdapter : RecyclerViewAdapter? = null

    private var compositeDisposable : CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        compositeDisposable = CompositeDisposable()
        // RecyclerView data gelemden önce oluşturmak mantıklı bir tercih olarka görünüyor bu projede

        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        loadData()
    }
    private fun loadData(){
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(CryptoAPI::class.java)

        compositeDisposable?.add(retrofit.getData()
            .subscribeOn(Schedulers.io())//bu sayede veriye kayıt olma işlemi main thread yormadan arkaplanda yapılacak
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::handlerResponse))

    }
    private fun handlerResponse(cryptoList: List<CryptoModel>){
        cryptoModels = ArrayList(cryptoList)
        cryptoModels?.let {
            recyclerViewAdapter = RecyclerViewAdapter(it,this@MainActivity)
            //it yerine cryptoModels!! da yazılabilirdi alternatif olarak
            recyclerView.adapter = recyclerViewAdapter
        }
    }

    override fun onItemClick(cryptoModel: CryptoModel) {
        Toast.makeText(this,"Cliked:${cryptoModel.currency}",Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable?.clear()
    }
    /*https://api.nomics.com/v1/prices?key=a82399be10cc5c1dadff681c8df1eeefc123916b
      a82399be10cc5c1dadff681c8df1eeefc123916b
     ***Özellikle büyük projelerde birden fazla kol olan yerlerde örneğin çok büyük bir  veri tabanları olduğunu düşünürsek
     ve bu veri tabanları ile API ler aracılığıyla veri alışverişi yapıldığı düşünülürse farklı farklı get/post requestlerle bir çok işlem yapmak durumunda kalabiliriz.
     Aynı anda birçok işlem yapıldıkça hafıza sorunları ortaya çıkmaya başlıyor.
     Asynctask yerine retrofit tercih etmemizin sebebi bu

     Retrofitte de bir çok call yapıp bunları hafızadan silmessek bir süre sonra sıkıntılar yaşamaya başlarız .
     Hafızayı daha iyi kullanabilmek için kodları daha verimli yapabilmek için rxJavadan faydalanıyoruz.
     rxjava varsa call yok demektir.

     Biz Call methodunu ilk olarak service içersinde kullanmıştık bunada call yerine observable rx olanı tercih ediyoruz .
     Observable ne demektir ? Gözlemlenebilir obje anlamına gelmektedir.
     Gelen verileri yayın yapan bir obje
     bir değişiklik geldiğinde bu objeye subscribe olan yani üye olan gözlemleyeni olana bilgi veren bir obje youtube kanalı mantığında


     RxJava ile kod yapımıza Disposible = Kullan at olarak ifade edilen bir yapı giriyor.
     *Activity yada fragment silindiğinde(yaşam döngüsünden çıktığında)
     bizim bu kolları(kodları) disponse etmemiz gerekiyor ki hafızada yer tutmasın
     rxJava içerisinde bulunan farklı farklı kullan atları birleştirip tek bir çöp haline getirebileceğimiz ve activite biterken direkt kurtulabileceğimiz bir obje compositeDisposible
       */
}