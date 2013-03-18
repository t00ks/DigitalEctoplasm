package org.tookscms.digitalectoplasm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.lang.Integer;

import org.odata4j.core.OEntity;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DataLoader {
	
	private LinearLayout layout;
	private Context currentContext;
	
	public void loadBulletins(View view, String method, Context context) {
		layout = (LinearLayout)view.findViewById(R.id.articleDetailPageContentLayout);
		currentContext = context;
		
		new DEFeed().execute(method, "0");		
	}
	
	private void populateView(List<OEntity> list) {
		if(list != null) {
			for(OEntity entity : list) {
				String summary = entity.getProperty("HtmlContent").getValue().toString();
				String title = entity.getProperty("Title").getValue().toString();
				
				TextView text = new TextView(currentContext);				
				text.setText(title);
				layout.addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				
				WebView web = new WebView(currentContext);			
				layout.addView(web, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
				web.loadData(summary, "text/html", null);
			}
		}		
	}
	
	private void populateException(Exception ex)
	{
		TextView error = new TextView(currentContext);
		error.setText(ex.getMessage());
		layout.addView(error, new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));		
	}
	
	public class DEFeed extends AsyncTask<String, Void, List<OEntity> > {

		private Exception exception;
		private int type;
		
		@Override
		protected List<OEntity> doInBackground(String... arg0) {
			try {
				this.type = Integer.parseInt(arg0[1]);
				ODataJerseyConsumer c = ODataJerseyConsumer.newBuilder("http://www.tooks-net.co.uk/DataService/PublicDataService.svc/").setFormatType(FormatType.JSON).build();
				List<OEntity> list = c.getEntities(arg0[0]).execute().toList();
				return list;
			} catch(Exception e) {
				this.exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<OEntity> list)
		{
			if(this.exception != null)
			{
				populateException(this.exception);			
			}
			else
			{
				switch(type)
				{
				case 0:
					populateView(list);
					break;
				case 1:
					populateGalleryView(list);
					break;
				}
			}
		}
	}
	
	
	private LayoutInflater inflater;
	private ViewGroup viewGroup; 
	
	public void loadGalleries(View view, LayoutInflater inflater, ViewGroup viewGroup, Context context)
	{
		this.layout = (LinearLayout)view.findViewById(R.id.articleDetailPageContentLayout);
		this.currentContext = context;
		this.viewGroup = viewGroup;
		this.inflater = inflater;
		
		new DEFeed().execute("GalleryBulletins", "1");		
	}

	private void populateGalleryView(List<OEntity> list) {
		if(list != null) {
			String baseUrl = "http://www.tooks-net.co.uk";
			for(OEntity entity : list) {
				String title = entity.getProperty("Title").getValue().toString();
				new DrawableFromUrlTask().execute(
						baseUrl + entity.getProperty("Image1_Thumb").getValue().toString(),
						baseUrl + entity.getProperty("Image2_Thumb").getValue().toString(),
						baseUrl + entity.getProperty("Image3_Thumb").getValue().toString(),
						title);
			}
		}		
	}

	@SuppressWarnings("deprecation")
	private void populateGalleryItem(List<Drawable> images, String title)
	{		
		View gallery = inflater.inflate(R.layout.bulletin_gallery, viewGroup, false);
		TextView text = (TextView)gallery.findViewById(R.id.title);
		text.setText(title);
		
		ImageView image1 = (ImageView)gallery.findViewById(R.id.image_1);
		image1.setBackgroundDrawable(images.get(0));		

		ImageView image2 = (ImageView)gallery.findViewById(R.id.image_2);
		image2.setBackgroundDrawable(images.get(1));		

		ImageView image3 = (ImageView)gallery.findViewById(R.id.image_3);
		image3.setBackgroundDrawable(images.get(2));

		layout.addView(gallery, new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
	}
	
	private class DrawableFromUrlTask extends AsyncTask<String, Void, List<Drawable>> {

		private Exception exception;
		private String title;
		
		@Override
		protected List<Drawable> doInBackground(String... params) {
			try {
				List<Drawable> list = new ArrayList<Drawable>();				
				
				for(int i = 0; i < 3; i++) {
					Drawable drawable = Drawable.createFromStream(((java.io.InputStream) new java.net.URL(params[i]).getContent()), "image" + i);
					list.add(drawable);
				}
				
				title = params[3];
				
				return list;
			} catch(Exception e) {
				this.exception = e;
				return null;
			}
		}

		@Override
		protected void onPostExecute(List<Drawable> images)
		{
			if(this.exception != null)
			{
				populateException(this.exception);
			}
			else 
			{
				populateGalleryItem(images, title);
			}
		}
	}
}
