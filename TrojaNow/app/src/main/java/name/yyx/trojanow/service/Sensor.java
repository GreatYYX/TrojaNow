package name.yyx.trojanow.service;

import android.content.Context;

import zh.wang.android.apis.yweathergetter4a.WeatherInfo;
import zh.wang.android.apis.yweathergetter4a.YahooWeather;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherExceptionListener;
import zh.wang.android.apis.yweathergetter4a.YahooWeatherInfoListener;

public class Sensor implements YahooWeatherInfoListener, YahooWeatherExceptionListener {
    private YahooWeather mYahooWeather = YahooWeather.getInstance(3000, 3000, true);
    private WeatherInfo weatherInfo = null;
    private boolean getInfo;
    private Context ctx;
    private ISensor listener = null;

    public Sensor(Context ctx) {
        getInfo = false;
        this.ctx = ctx;

        mYahooWeather.setExceptionListener(this);
        mYahooWeather.setNeedDownloadIcons(true);
        mYahooWeather.setUnit(YahooWeather.UNIT.CELSIUS);
        mYahooWeather.setSearchMode(YahooWeather.SEARCH_MODE.GPS);
    }

    public Sensor(Context ctx, ISensor listener) {
        this(ctx);
        this.listener = listener;
    }

    @Override
    public void onFailConnection(Exception e) {
        getInfo = false;
        if(listener != null) {
            listener.onFail();
        }
    }

    @Override
    public void onFailParsing(Exception e) {
        getInfo = false;
        if(listener != null) {
            listener.onFail();
        }
    }

    @Override
    public void onFailFindLocation(Exception e) {
        getInfo = false;
        if(listener != null) {
            listener.onFail();
        }
    }

    @Override
    public void gotWeatherInfo(WeatherInfo weatherInfo) {
        if(weatherInfo != null) {
            getInfo = true;
            this.weatherInfo = weatherInfo;
        }
        if(listener != null) {
            listener.onDataReceived();
        }
    }

    public void update() {
        mYahooWeather.queryYahooWeatherByGPS(ctx, this);
    }

    public void removeListener() {
        this.listener = null;
    }

    public boolean canGetInfo() {
        return getInfo;
    }

    public int getTemperature() {
        return weatherInfo.getCurrentTemp();
    }

    public String[] getLocation() {
        return new String[] {weatherInfo.getConditionLat(), weatherInfo.getConditionLon()};
    }
}
