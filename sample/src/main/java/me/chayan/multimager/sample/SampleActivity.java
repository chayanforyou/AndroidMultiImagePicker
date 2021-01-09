package me.chayan.multimager.sample;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.ArrayList;

import me.chayan.multimager.activities.BaseActivity;
import me.chayan.multimager.activities.GalleryActivity;
import me.chayan.multimager.activities.MultiCameraActivity;
import me.chayan.multimager.adapters.GalleryImagesAdapter;
import me.chayan.multimager.sample.databinding.ActivitySampleBinding;
import me.chayan.multimager.utils.Constants;
import me.chayan.multimager.utils.Image;
import me.chayan.multimager.utils.Params;
import me.chayan.multimager.utils.Utils;

/**
 * Created by Chayan on 09/01/2021.
 */
public class SampleActivity extends BaseActivity {

    int selectedColor;
    int darkenedColor;

    private ActivitySampleBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_sample);
        binding = ActivitySampleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        selectedColor = fetchAccentColor();
        darkenedColor = Utils.getDarkColor(selectedColor);

        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                binding.customThemeButton,
                binding.multiCaptureButton,
                binding.multiPickerButton,
                binding.appName);

        binding.multiCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.hasCameraHardware(SampleActivity.this))
                    initiateMultiCapture();
                else
                    Utils.showLongSnack(binding.parentLayout, "Sorry. Your device does not have a camera.");
            }
        });

        binding.multiPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMultiPicker();
            }
        });

        binding.customThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCustomTheme();
            }
        });

        binding.github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToUrl();
            }
        });
    }

    private int fetchAccentColor() {
        TypedValue typedValue = new TypedValue();
        TypedArray a = obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorAccent});
        int color = a.getColor(0, 0);
        a.recycle();
        return color;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.TYPE_MULTI_CAPTURE:
            case Constants.TYPE_MULTI_PICKER:
                handleResponseIntent(intent);
                break;
        }
    }

    private int getColumnCount() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        float thumbnailDpWidth = getResources().getDimension(R.dimen.thumbnail_width) / displayMetrics.density;
        return (int) (dpWidth / thumbnailDpWidth);
    }

    private void handleResponseIntent(Intent intent) {
        ArrayList<Image> imagesList = intent.getParcelableArrayListExtra(Constants.KEY_BUNDLE_LIST);
        binding.recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager mLayoutManager = new StaggeredGridLayoutManager(getColumnCount(), GridLayoutManager.VERTICAL);
        mLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        binding.recyclerView.setLayoutManager(mLayoutManager);
        GalleryImagesAdapter imageAdapter = new GalleryImagesAdapter(this, imagesList, getColumnCount(), new Params());
        binding.recyclerView.setAdapter(imageAdapter);
    }

    private void navigateToUrl() {
        // Original Library From: https://github.com/vansikrishna/Multimager.git
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/chayanforyou/Multimager.git")));
    }

    private void setCustomTheme() {
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(selectedColor)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton("OK", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeColor(selectedColor);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeColor(int selectedColor) {
        this.selectedColor = selectedColor;
        this.darkenedColor = Utils.getDarkColor(selectedColor);
        Utils.showShortSnack(binding.parentLayout, "New color selected #" + Integer.toHexString(selectedColor).toUpperCase());
        //Utils.setViewsColorStateList(customThemeButton, selectedColor, darkenedColor);
        //Utils.setViewsColorStateList(multiCaptureButton, selectedColor, darkenedColor);
        //Utils.setViewsColorStateList(multiPickerButton, selectedColor, darkenedColor);

        Utils.setViewsColorStateList(selectedColor, darkenedColor,
                binding.customThemeButton,
                binding.multiCaptureButton,
                binding.multiPickerButton,
                binding.appName);
    }

    private void initiateMultiCapture() {
        Intent intent = new Intent(this, MultiCameraActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_CAPTURE);
    }

    private void initiateMultiPicker() {
        Intent intent = new Intent(this, GalleryActivity.class);
        Params params = new Params();
        params.setCaptureLimit(10);
        params.setPickerLimit(10);
        params.setToolbarColor(selectedColor);
        params.setActionButtonColor(selectedColor);
        params.setButtonTextColor(selectedColor);
        intent.putExtra(Constants.KEY_PARAMS, params);
        startActivityForResult(intent, Constants.TYPE_MULTI_PICKER);
    }

}
