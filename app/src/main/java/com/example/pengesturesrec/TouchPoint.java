package com.example.pengesturesrec;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

public final class TouchPoint {
    public float a;
    public float b;
    public float c;
    public float d;
    public float e;
    public long f;
    public boolean g;

    public TouchPoint(float var1, float var2) {
        this.a = var1;
        this.b = var2;
        this.c = 0.0F;
        this.f = 0L;
        this.g = false;
    }

    public TouchPoint(float var1, float var2, float var3) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
        this.f = 0L;
        this.g = false;
    }

    public TouchPoint(float var1, float var2, long var3) {
        this.a = var1;
        this.b = var2;
        this.c = 0.0F;
        this.f = var3;
        this.g = false;
    }

    public TouchPoint(float var1, float var2, float var3, long var4) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
        this.f = var4;
        this.g = false;
    }

    public TouchPoint(float var1, float var2, float var3, long var4, boolean var6) {
        this.a = var1;
        this.b = var2;
        this.c = var3;
        this.f = var4;
        this.g = var6;
    }

    public float getX() {
        return this.a;
    }

    public void setX(float var1) {
        this.a = var1;
    }

    public float getY() {
        return this.b;
    }

    public void setY(float var1) {
        this.b = var1;
    }

    public float getPressure() {
        return this.c;
    }

    public void setPressure(float var1) {
        this.c = var1;
    }

    public long getTime() {
        return this.f;
    }

    public void setTime(long var1) {
        this.f = var1;
    }

    public boolean isHistory() {
        return this.g;
    }

    public void setHistory(boolean var1) {
        this.g = var1;
    }

    public float getTilt() {
        return this.d;
    }

    public void setTilt(float var1) {
        this.d = var1;
    }

    public float getOrientation() {
        return this.e;
    }

    public void setOrientation(float var1) {
        this.e = var1;
    }

}
