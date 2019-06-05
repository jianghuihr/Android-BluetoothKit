package com.hehuibio.ble.file.model;

public class Temp {

    private float mod;
    private float top;
    private float env;

    public Temp(float mod, float top, float env) {
        this.mod = mod;
        this.top = top;
        this.env = env;
    }

    public float getMod() {
        return mod;
    }

    public void setMod(float mod) {
        this.mod = mod;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public float getEnv() {
        return env;
    }

    public void setEnv(float env) {
        this.env = env;
    }
}
