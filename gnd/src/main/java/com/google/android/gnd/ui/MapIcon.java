/*
 * Copyright 2018 Google LLC
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

package com.google.android.gnd.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gnd.R;

// TODO: Move to common, rename Marker.
public class MapIcon {
  private final Context context;
  private final int color;

  public MapIcon(Context context, @Nullable String colorHexCode) {
    this.context = context;
    this.color = getIconColor(context, colorHexCode);
  }

  public BitmapDescriptor getBitmap() {
    Drawable outline = AppCompatResources.getDrawable(context, R.drawable.ic_marker_outline);
    Drawable fill = AppCompatResources.getDrawable(context, R.drawable.ic_marker_fill);
    Drawable overlay = AppCompatResources.getDrawable(context, R.drawable.ic_marker_overlay);
    // TODO: Define scale in resources.
    // TODO: Adjust size based on zoom level and selection state.
    float scale = 1.8f;
    int width = (int) (outline.getIntrinsicWidth() * scale);
    int height = (int) (outline.getIntrinsicHeight() * scale);
    Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    Canvas canvas = new Canvas(bitmap);
    outline.setBounds(0, 0, width, height);
    outline.draw(canvas);
    fill.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    fill.setBounds(0, 0, width, height);
    fill.draw(canvas);
    overlay.setBounds(0, 0, width, height);
    overlay.draw(canvas);
    // TODO: Cache rendered bitmaps.
    return BitmapDescriptorFactory.fromBitmap(bitmap);
  }

  private static int getIconColor(Context context, String colorHexCode) {
    try {
      return Color.parseColor(colorHexCode);
    } catch (IllegalArgumentException e) {
      return context.getResources().getColor(R.color.colorMapAccent);
    }
  }
}
