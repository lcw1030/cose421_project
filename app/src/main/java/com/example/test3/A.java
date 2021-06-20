package com.example.test3;

import android.os.Build;
import android.util.JsonReader;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class A {
    private static A instance = null;
    private List<int[]> locD, posD, wgtD;
    private int n = 10;
    private List<int[]> testD = null;
    private final boolean isTest = false;

    public enum Type {
        LOC (1),
        POS (4),
        WGT (2);

        public final int dS;

        public static Type fromInt(int n) {
            switch (n) {
                case 0:
                    return LOC;
                case 1:
                    return POS;
                case  2:
                    return WGT;
            }

            return null;
        }

        private Type(int dS) {
            this.dS = dS;
        }
    }

    private A () {
        locD = new ArrayList<>();
        posD = new ArrayList<>();
        wgtD = new ArrayList<>();
        testD = new ArrayList<>();

        if (isTest) {
            int[][] d = {
                    { 80, 153, 113, 70, 0, 1, 0 },
                    { 80, 100, 0, 156, 0, 1, 0 },
                    { 80, 200, 0, 250, 0, 1, 0 },
                    { 80, 300, 0, 70, 0, 1, 0 },
                    { 80, 1, 0, 1, 0, 1, 0 },
                    { 80, 1, 0, 0, 0, 0, 0 },
                    { 80, 1, 0, 0, 0, 0, 0 },
                    { 80, 1, 0, 0, 0, 0, 0 },
                    { 80, 1, 0, 0, 0, 0, 0 },
                    { 80, 1, 0, 0, 0, 0, 0 },
            };
            testD.addAll(Arrays.asList(d));
        }
    }

    private void align(List<int[]> pd, int dS, int n) {
        if (pd.size() < n) {
            int pad = n - pd.size();
            int[] padding = new int[dS];

            for (int i = 0; i < pad; ++i)
                pd.add(padding);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private List<int[]> concat(List<int[]> a, List<int[]> b, List<int[]> c) {
        List<int[]> r = new ArrayList<>();
        for (int i = 0; i < a.size(); ++i) {
            int[] con0 = IntStream.concat(Arrays.stream(a.get(i)), Arrays.stream(b.get(i))).toArray();
            int[] conF = IntStream.concat(Arrays.stream(con0), Arrays.stream(c.get(i))).toArray();
            r.add(conF);
        }

        return r;
    }

    public static A getInstance() {
        if (instance == null) {
            instance = new A();
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public synchronized List<int[]> getN(int n) {
        if (isTest) {
            n=(n+1) % 10;
            List<int[]> arr = new ArrayList<>();
            arr.add(testD.get(n));
            return arr;
           // return new ArrayList<int[]>(testD.subList(0, n));
        }

        int m = n;
        if (locD.size() < m) {
            m = locD.size();
        }
        if (posD.size() < m) {
            m = posD.size();
        }
        if (wgtD.size() < m) {
            m = wgtD.size();
        }

        List<int[]> locPD = new ArrayList<>(locD.subList(0, m));
        List<int[]> posPD = new ArrayList<>(posD.subList(0, m));
        List<int[]> wgtPD = new ArrayList<>(wgtD.subList(0, m));
        align(locPD, Type.LOC.dS, n);
        align(posPD, Type.POS.dS, n);
        align(wgtPD, Type.WGT.dS, n);

        List<int[]> r = concat(locPD, posPD, wgtPD);

        return r;
    }

    public synchronized void add(Type type, int[] d) {
        Log.i("msgAd", Arrays.toString(d));
        switch (type) {
            case LOC:
                Log.i("msgAdLOC", Arrays.toString(d));
                if (d.length == Type.LOC.dS) {
                    locD.add(d);
                }
                break;
            case POS:
                if (d.length == Type.POS.dS) {
                    posD.add(d);
                }
                break;
            case WGT:
                if (d.length == Type.WGT.dS) {
                    wgtD.add(d);
                }
                break;
        }


    }
}

