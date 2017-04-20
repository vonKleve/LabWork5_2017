package com.example.dell.myapplication;

import android.opengl.GLSurfaceView.Renderer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import java.nio.ShortBuffer;

public class MyGLRenderer implements Renderer {

    // Real Coordinates
    private float []x;
    private float []y;
    private float []z;

    // Pattern coordinates
    private FloatBuffer mVFLBuffer = null;
    private FloatBuffer xFLBuffer, yFLBUFFER, zFLBuffer;
    private ShortBuffer xSHBuffer, ySHBuffer, zSHBuffer;
    private ShortBuffer mVSHBuffer = null;

    // Angles
    public float xAngle = 0.0f, yAngle = 0.0f, zAngle = 0.0f;

    public MyGLRenderer(float[] _x, float[] _y, float[] _z){
        x = _x;
        y = _y;
        z = _z;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        SetAllBuffers();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glFrustumf(-width/height, width/height, -1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity();
    }

    private void Draw(GL10 gl, float[] color, FloatBuffer FlBuffer, ShortBuffer ShBuffer)
    {
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, FlBuffer);
        gl.glColor4f(color[0], color[1], color[2], color[3]);
        gl.glRotatef(xAngle, 1, 0, 0);
        gl.glRotatef(yAngle, 0, 1, 0);
        gl.glRotatef(zAngle, 0, 0, 1);
        gl.glDrawElements(GL10.GL_LINES, ShBuffer.capacity(), GL10.GL_UNSIGNED_SHORT, ShBuffer);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glFrustumf(-1, 1, -1, 1, 0.1f, 2);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        float[] color = new float[4];
        color[0] = 0.3f; color[1] = 0.5f; color[2] = 0.9f; color[3] = 1.0f;
        Draw(gl,color,mVFLBuffer,mVSHBuffer);
        color[0] = 1.0f; color[1] = 0.0f; color[2] = 0.0f; color[3] = 1.0f;
        Draw(gl,color,xFLBuffer,xSHBuffer);
        color[0] = 0.0f; color[1] = 1.0f;
        Draw(gl,color,yFLBUFFER,ySHBuffer);
        color[1] = 0.0f; color[2] = 1.0f;
        Draw(gl,color,zFLBuffer,zSHBuffer);
    }

    private void SetAxis(int bias, float []axis, float []defaultAxis)
    {
        int realPlace;
        for(int i = 0; i < axis.length; i++)
        {
            realPlace = i + bias;
            if(i + bias >= axis.length) realPlace = (i + bias) % axis.length;
            axis[realPlace] = defaultAxis[i];
        }
    }

    private void SetFloatBuffers(float []xAxis, float []yAxis, float []zAxis, float []mVertexList)
    {
        ByteBuffer vbb = ByteBuffer.allocateDirect(xAxis.length*4);
        vbb.order(ByteOrder.nativeOrder());
        xFLBuffer = vbb.asFloatBuffer();
        xFLBuffer.put(xAxis);
        xFLBuffer.position(0);

        vbb = ByteBuffer.allocateDirect(yAxis.length*4);
        vbb.order(ByteOrder.nativeOrder());
        yFLBUFFER = vbb.asFloatBuffer();
        yFLBUFFER.put(yAxis);
        yFLBUFFER.position(0);

        vbb = ByteBuffer.allocateDirect(zAxis.length*4);
        vbb.order(ByteOrder.nativeOrder());
        zFLBuffer = vbb.asFloatBuffer();
        zFLBuffer.put(zAxis);
        zFLBuffer.position(0);

        vbb = ByteBuffer.allocateDirect(mVertexList.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        mVFLBuffer = vbb.asFloatBuffer();
        mVFLBuffer.put(mVertexList);
        mVFLBuffer.position(0);

    }

    private void SetShortBuffers(short []xAxis, short []yAxis, short []zAxis, short []mVertexList)
    {
        ByteBuffer gbibb = ByteBuffer.allocateDirect(xAxis.length * 2);
        gbibb.order(ByteOrder.nativeOrder());
        xSHBuffer = gbibb.asShortBuffer();
        xSHBuffer.put(xAxis);
        xSHBuffer.position(0);


        gbibb = ByteBuffer.allocateDirect(yAxis.length * 2);
        gbibb.order(ByteOrder.nativeOrder());
        ySHBuffer = gbibb.asShortBuffer();
        ySHBuffer.put(yAxis);
        ySHBuffer.position(0);

        gbibb = ByteBuffer.allocateDirect(zAxis.length * 2);
        gbibb.order(ByteOrder.nativeOrder());
        zSHBuffer = gbibb.asShortBuffer();
        zSHBuffer.put(zAxis);
        zSHBuffer.position(0);

        gbibb = ByteBuffer.allocateDirect(mVertexList.length * 2);
        gbibb.order(ByteOrder.nativeOrder());
        mVSHBuffer = gbibb.asShortBuffer();
        mVSHBuffer.put(mVertexList);
        mVSHBuffer.position(0);

    }

    private void SetAllBuffers(){
        int index = 0;
        float[] xAxis = new float[12];
        float[] yAxis = new float[12];
        float[] zAxis = new float[12];
        float[] defaultAxis = new float[12];

        defaultAxis[0]=-1.0f; defaultAxis[1]=0.0f;    defaultAxis[2]=0.0f;
        defaultAxis[3]=1.0f;  defaultAxis[4]=0.0f;    defaultAxis[5]=0.0f;
        defaultAxis[6]=0.9f;  defaultAxis[7]=-0.05f;  defaultAxis[8]=0.0f;
        defaultAxis[9]=0.9f;  defaultAxis[10]=0.05f;  defaultAxis[11]=0.0f;


        // set xyz
        SetAxis(0,xAxis,defaultAxis);
        SetAxis(1,yAxis,defaultAxis);
        SetAxis(2,zAxis,defaultAxis);

        float[] vertexlist = new float[(x.length+1)*3];
        index=3;
        vertexlist[0]=vertexlist[1]=vertexlist[2]=0.0f;
        for (int i = 0; i< x.length; ++i){
            vertexlist[index]=vertexlist[index-3]+ x[i]; index++;
            vertexlist[index]=vertexlist[index-3]+ y[i]; index++;
            vertexlist[index]=vertexlist[index-3]+ z[i]; index++;
        }
        SetFloatBuffers(xAxis,yAxis,zAxis,vertexlist);



        short[] xAxisIndexList = new short[6];
        short[] yAxisIndexList;
        short[] zAxisIndexList;
        short[] graphBorderIndexList = new short[2* x.length];
        index = 0;

        xAxisIndexList[index++]=0;
        xAxisIndexList[index++]=1;
        xAxisIndexList[index++]=2;
        xAxisIndexList[index++]=1;
        xAxisIndexList[index++]=1;
        xAxisIndexList[index]=3;

        yAxisIndexList = xAxisIndexList.clone();
        zAxisIndexList = xAxisIndexList.clone();

        index = 0;
        short index2=0;
        while (x.length > index2){
            graphBorderIndexList[index++] = index2;
            index2++;
            graphBorderIndexList[index++] = index2;

        }
        SetShortBuffers(xAxisIndexList,yAxisIndexList,zAxisIndexList,graphBorderIndexList);
    }

}