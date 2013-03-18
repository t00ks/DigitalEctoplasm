package org.tookscms.digitalectoplasm;

import java.util.List;

import org.odata4j.core.OEntity;
import org.odata4j.format.FormatType;
import org.odata4j.jersey.consumer.ODataJerseyConsumer;

import android.os.AsyncTask;


public class DEFeed extends AsyncTask<String, Void, List<OEntity> > {

	private Exception exception;
	
	@Override
	protected List<OEntity> doInBackground(String... arg0) {
		try {
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
			String s = this.exception.getMessage();			
		}
	}
}
