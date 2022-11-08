// MusicAidlInterface.aidl
package com.prosabdev.fluidmusic;

// Declare any non-default types here with import statements

interface MusicAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);

    //Setters
    boolean play(int position);
    boolean pause();
    boolean resume();
    boolean stop();
    boolean next();
    boolean prev();
    boolean shuffle(String shuffleValue);
    boolean repeat(String shuffleValue);
    boolean seekTo(long seekTime);
    boolean favorite(boolean favoriteValue);

    void updateQueueList(String queueListType);
}