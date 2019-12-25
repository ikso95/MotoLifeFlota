package com.example.motolifeflota.PhotosRecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.example.motolifeflota.MainActivity;
import com.example.motolifeflota.R;


public class SliderAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;

    private String path;
    private int nrOfPictures;

    public SliderAdapter(Context ctx){
        this.context = ctx;
    }

    public SliderAdapter(Context ctx, String path, int nrOfPictures)
    {
        this.context=ctx;
        this.path=path;
        this.nrOfPictures=nrOfPictures;
    }




    @Override
    public int getCount() {
        return nrOfPictures;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {


        layoutInflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout,container,false);

        ImageView slideImageView = view.findViewById(R.id.imageView2);



        Bitmap myBitmap = BitmapFactory.decodeFile(path+String.valueOf(position)+".jpg");
        Matrix matrix = new Matrix();           //obrocenie zdjecia o 90 stopni
        matrix.postRotate(90);
        Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

        slideImageView.setImageBitmap(rotatedBitmap);

        container.addView(view);

        return  view ;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {

        container.removeView((RelativeLayout)object);

    }
}
