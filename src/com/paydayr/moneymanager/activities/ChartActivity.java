package com.paydayr.moneymanager.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.paydayr.moneymanager.R;
import com.paydayr.moneymanager.exception.BusinessException;
import com.paydayr.moneymanager.facade.MoneyManagerFacade;
import com.paydayr.moneymanager.holders.TotalByMonthHolder;
import com.paydayr.moneymanager.util.Converter;

public class ChartActivity extends Activity {

	public static final String TYPE = "type";

	private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();

	private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();

	private XYSeries mCurrentSeries;

	private XYSeriesRenderer mCurrentRenderer;

	private String mDateFormat;

	private GraphicalView mChartView;

	private int index = 0;
	
	private MoneyManagerFacade moneyManagerFacade;

	@Override
	protected void onRestoreInstanceState(Bundle savedState) {
		super.onRestoreInstanceState(savedState);
		mDataset = (XYMultipleSeriesDataset) savedState.getSerializable("dataset");
		mRenderer = (XYMultipleSeriesRenderer) savedState.getSerializable("renderer");
		mCurrentSeries = (XYSeries) savedState.getSerializable("current_series");
		mCurrentRenderer = (XYSeriesRenderer) savedState.getSerializable("current_renderer");
		mDateFormat = savedState.getString("date_format");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("dataset", mDataset);
		outState.putSerializable("renderer", mRenderer);
		outState.putSerializable("current_series", mCurrentSeries);
		outState.putSerializable("current_renderer", mCurrentRenderer);
		outState.putString("date_format", mDateFormat);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chart_page);

		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(getResources().getColor(R.color.list_second_color));
		mRenderer.setAxisTitleTextSize(22);
		mRenderer.setXTitle("Month");
		mRenderer.setYTitle("Total ($)");
		mRenderer.setChartTitleTextSize(30);
		mRenderer.setLabelsTextSize(22);
		mRenderer.setLegendTextSize(24);
		mRenderer.setMargins(new int[] { 50, 40, 20, 20 });//top, left, botton, right
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setPointSize(10);
		mRenderer.setChartTitle("Chart of Monthly Expenses");
		mRenderer.setShowAxes(true);
		mRenderer.setShowGrid(true);
		mRenderer.setMarginsColor(Color.WHITE);
		mRenderer.setLabelsColor(Color.BLACK);
		mRenderer.setAxesColor(Color.BLACK);
		mRenderer.setGridColor(getResources().getColor(R.color.list_main_color));

		String seriesTitle = "General " + (mDataset.getSeriesCount() + 1);
		XYSeries series = new XYSeries(seriesTitle);
		mDataset.addSeries(series);
		mCurrentSeries = series;
		XYSeriesRenderer renderer = new XYSeriesRenderer();
		mRenderer.addSeriesRenderer(renderer);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setColor(getResources().getColor(R.color.list_dark_color));
		renderer.setFillPoints(true);
		mCurrentRenderer = renderer;
		
		//OBTEM OS VALORES DO GRAFICO
		List<TotalByMonthHolder> totais = new ArrayList<TotalByMonthHolder>();
		moneyManagerFacade = new MoneyManagerFacade();
		try {
			totais = moneyManagerFacade.getTotalByMonth(this);
		} catch (BusinessException e1) {
			Log.e(this.getClass().getName(), e1.getMessage());
			Toast.makeText(this, "[ChartActivity] Error getting total expenditures per month. Contact the system administrator.", Toast.LENGTH_LONG).show();
		}

		for( TotalByMonthHolder holder : totais ){
			mCurrentSeries.add(holder.getMes(), holder.getTotal());	
		}
		
		int length = mRenderer.getSeriesRendererCount();
	    for (int i = 0; i < length; i++) {
	      SimpleSeriesRenderer seriesRenderer = mRenderer.getSeriesRendererAt(i);
	      seriesRenderer.setDisplayChartValues(true);
	      seriesRenderer.setChartValuesTextSize(30);
	      seriesRenderer.setChartValuesSpacing(25);
	      seriesRenderer.setChartValuesTextAlign(Align.CENTER);
	    }
		
		//////////////////////////////////////////////////
		if (mChartView == null) {
			LinearLayout layout = (LinearLayout) findViewById(R.id.chart_layout);
			mChartView = ChartFactory.getLineChartView(this, mDataset, mRenderer);
			mRenderer.setClickEnabled(true);
			mRenderer.setSelectableBuffer(100);
			mChartView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
//					double[] xy = mChartView.toRealPoint(0);
					if (seriesSelection == null) {
//						Toast.makeText(ChartActivity.this, "No chart element was clicked", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ChartActivity.this, 
//										"Chart element in series index "
//										+ seriesSelection.getSeriesIndex()
//										" data point index "
//										+ seriesSelection.getPointIndex()
//										+ " was clicked"
										getMonth(seriesSelection.getXValue()) + ", "
										+ Converter.getDecimalFormat().format(seriesSelection.getValue())
//										+ " clicked point value X="
//										+ (float) xy[0] + ", Y="
//										+ (float) xy[1]
										, Toast.LENGTH_SHORT)
								.show();
					}
				}
			});
//			mChartView.setOnLongClickListener(new View.OnLongClickListener() {
//				@Override
//				public boolean onLongClick(View v) {
//					SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();
//					if (seriesSelection == null) {
//						Toast.makeText(ChartActivity.this, "No chart element was long pressed", Toast.LENGTH_SHORT).show();
//						return false;
//					} else {
//						Toast.makeText(ChartActivity.this, "Chart element in series index "
//										+ seriesSelection.getSeriesIndex()
//										+ " data point index "
//										+ seriesSelection.getPointIndex()
//										+ " was long pressed",
//								Toast.LENGTH_SHORT).show();
//						return true;
//					}
//				}
//			});
			mChartView.addZoomListener(new ZoomListener() {
				public void zoomApplied(ZoomEvent e) {
					String type = "out";
					if (e.isZoomIn()) {
						type = "in";
					}
					System.out.println("Zoom " + type + " rate "
							+ e.getZoomRate());
				}

				public void zoomReset() {
					System.out.println("Reset");
				}
			}, true, true);
			mChartView.addPanListener(new PanListener() {
				public void panApplied() {
					System.out.println("New X range=["
							+ mRenderer.getXAxisMin() + ", "
							+ mRenderer.getXAxisMax() + "], Y range=["
							+ mRenderer.getYAxisMax() + ", "
							+ mRenderer.getYAxisMax() + "]");
				}
			});
			layout.addView(mChartView, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
//			boolean enabled = mDataset.getSeriesCount() > 0;
		} else {
			mChartView.repaint();
		}
		////////////////////////////////////////////////
		
		if (mChartView != null) {
			mChartView.repaint();
		}
		Bitmap bitmap = mChartView.toBitmap();
		try {
			File file = new File(Environment.getExternalStorageDirectory(), "MoneyManager-Chart" + index++ + ".png");
			FileOutputStream output = new FileOutputStream(file);
			bitmap.compress(CompressFormat.PNG, 100, output);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}

	@Override
	protected void onResume() {
		super.onResume();

	}
	
	private String getMonth(double monthInt){
		String mes = null;
		switch(Double.valueOf(monthInt).intValue()){
			case 1:
				mes = "January";
				break;
			case 2:
				mes = "February";
				break;
			case 3:
				mes = "March";
				break;
			case 4:
				mes = "April";
				break;
			case 5:
				mes = "May";
				break;
			case 6:
				mes = "June";
				break;
			case 7:
				mes = "July";
				break;
			case 8:
				mes = "August";
				break;
			case 9:
				mes = "September";
				break;
			case 10:
				mes = "October";
				break;
			case 11:
				mes = "November";
				break;
			case 12:
				mes = "December";
				break;
		}
		return mes;
	}
}
