package com.example.dell.myapplication;

import android.opengl.GLSurfaceView;

import android.app.Activity;
import android.view.MotionEvent;

public class mGLSurfaceView extends GLSurfaceView {
    MyGLRenderer renderer;
    private float previousX;
    private float previousY;

    public mGLSurfaceView(Activity aActivity, float []x, float []y, float []z) {
        super(aActivity);
        renderer = new MyGLRenderer(x,y,z);
        setRenderer(renderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);     // The renderer only renders when the surface is created, or when requestRender() is called
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getY(), y = event.getX();
        if(event.getAction() == MotionEvent.ACTION_MOVE)
        {
                float dx = x - previousX;
                float dy = y - previousY;

                if (y < getHeight() / 2) dx = dx * -1 ;
                if (x > getWidth() / 2) dy = dy * -1 ;

                renderer.xAngle +=dx/3;
                renderer.yAngle +=dy/3;

                requestRender();
        }
        previousX = x;
        previousY = y;
        return true;
    }
}
