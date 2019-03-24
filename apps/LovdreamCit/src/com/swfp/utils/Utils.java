
package com.swfp.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Utils {
    public Utils() {
        super();
    }

    public static float byte2float(byte[] buf, int index) {
        return Float.intBitsToFloat(((int)((((long)((((int)((((long)((((int)((((long)(buf[index] & 255)))
                 | (((long)buf[index + 1])) << 8))) & 65535))) | (((long)buf[index + 2])) << 16))) & 
                16777215))) | (((long)buf[index + 3])) << 24)));
    }

    public static int byteArray2Int(byte[] buf) {
        int v2 = 0;
        int v0;
        for(v0 = 0; v0 < 4; ++v0) {
            v2 += (buf[v0] & 255) << v0 * 8;
        }

        return v2;
    }

    public static int[] byteArray2IntArray(byte[] buf, int len) {
        if(len < 4) {
            throw new RuntimeException("byteArrayToIntArray: len is less than 4");
        }

        if(len % 4 != 0) {
            throw new RuntimeException("byteArrayToIntArray: len is not multiples of 4");
        }

        int[] v1 = new int[len / 4];
        int v3 = 0;
        int v0 = 0;

        while(v0 < len) {
            v3 += (buf[v0] & 255) << v0 * 8;
            if(v0 % 4 == 3) {
                v1[v0 / 4] = v3;
                v3 = 0;
            }

            ++v0;
        }

        return v1;
    }

    public static float[] byteArray2floatArray(byte[] buf, int len) {
        if(buf.length < len * 4) {
            throw new RuntimeException("function: byteArray2floatArray buf.length = " + buf.length + 
                    " but len = " + len);
        }

        float[] v4 = new float[len];
        int v2 = 0;
        int v1;
        for(v1 = 0; v1 < len * 4; ++v1) {
            int v0 = v1 / 4;
            switch(v1 % 4) {
                case 0: {
                    v2 = buf[v1] & 255;
                    break;
                }
                case 1: {
                    v2 = (((int)((((long)v2)) | (((long)buf[v1])) << 8))) & 65535;
                    break;
                }
                case 2: {
                    v2 = (((int)((((long)v2)) | (((long)buf[v1])) << 16))) & 16777215;
                    break;
                }
                case 3: {
                    v2 = ((int)((((long)v2)) | (((long)buf[v1])) << 24));
                    v4[v0] = Float.intBitsToFloat(v2);
                    break;
                }
            }
        }

        return v4;
    }

    public static byte[] kvalueToBmp(float[] fkv, int len) {
        byte[] v0 = new byte[len];
        int v1;
        for(v1 = 0; v1 < len; ++v1) {
            v0[v1] = ((byte)((((int)(fkv[v1] * 10000f))) & 255));
        }

        int v4 = v0[0];
        int v3 = v0[0];
        for(v1 = 0; v1 < len; ++v1) {
            if(v4 > v0[v1]) {
                v4 = v0[v1];
            }

            if(v3 < v0[v1]) {
                v3 = v0[v1];
            }
        }

        int v2 = v3 - v4 + 1;
        for(v1 = 0; v1 < len; ++v1) {
            v0[v1] = ((byte)((v0[v1] - v4) * 255 / v2));
        }

        return v0;
    }

    public static Bitmap rotateBitmap(Bitmap origin, float alpha) {
        Bitmap v0 = null;
        if(origin == null) {
            return v0;
        }

        int v3 = origin.getWidth();
        int v4 = origin.getHeight();
        Matrix v5 = new Matrix();
        v5.setRotate(alpha);
        Bitmap v7 = Bitmap.createBitmap(origin, 0, 0, v3, v4, v5, false);
        if(v7.equals(origin)) {
            return v7;
        }

        origin.recycle();
        return v7;
    }

	/*
    public static void saveByteArrayToLocal(byte[] buf, int len, File file) {
        FileOutputStream v3;
        FileOutputStream v2 = null;
        try {
            v3 = new FileOutputStream(file);
            goto label_3;
        }
        catch(Throwable v4) {
        }
        catch(IOException v1) {
            goto label_13;
            try {
            label_3:
                v3.write(buf, 0, len);
                v3.flush();
                if(v3 == null) {
                    return;
                }

                goto label_6;
            }
            catch(Throwable v4) {
                v2 = v3;
            }
            catch(IOException v1) {
                v2 = v3;
                try {
                label_13:
                    v1.printStackTrace();
                    if(v2 == null) {
                        return;
                    }
                }
                catch(Throwable v4) {
                    goto label_29;
                }

                try {
                    v2.close();
                }
                catch(IOException v1) {
                    v1.printStackTrace();
                }

                return;
            }
            catch(FileNotFoundException v0) {
                v2 = v3;
                try {
                label_21:
                    v0.printStackTrace();
                    if(v2 == null) {
                        return;
                    }
                }
                catch(Throwable v4) {
                    goto label_29;
                }

                try {
                    v2.close();
                }
                catch(IOException v1) {
                    v1.printStackTrace();
                }

                return;
            }
        }
        catch(FileNotFoundException v0) {
            goto label_21;
        }

    label_29:
        if(v2 != null) {
            try {
                v2.close();
            }
            catch(IOException v1) {
                v1.printStackTrace();
            }
        }

        throw v4;
        try {
        label_6:
            v3.close();
        }
        catch(IOException v1) {
            v1.printStackTrace();
        }
    }
	*/

    public static byte[] translateImageCode(byte[] imput, int col, int row) {
        byte[] v6 = new byte[]{66, 77, 0, 0, 0, 0, 0, 0, 0, 0, 54, 4, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0, 1, 0, 8, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
                0, 0, 0, 0};
        byte[] v0 = new byte[1078];
        byte[] v3 = new byte[col * row + 1078];
        System.arraycopy(v6, 0, v0, 0, v6.length);
        long v4 = ((long)col);
        v0[18] = ((byte)(((int)(255 & v4))));
        v4 >>= 8;
        v0[19] = ((byte)(((int)(255 & v4))));
        v4 >>= 8;
        v0[20] = ((byte)(((int)(255 & v4))));
        v0[21] = ((byte)(((int)(255 & v4 >> 8))));
        v4 = ((long)row);
        v0[22] = ((byte)(((int)(255 & v4))));
        v4 >>= 8;
        v0[23] = ((byte)(((int)(255 & v4))));
        v4 >>= 8;
        v0[24] = ((byte)(((int)(255 & v4))));
        v0[25] = ((byte)(((int)(255 & v4 >> 8))));
        int v2 = 0;
        int v1;
        for(v1 = 54; v1 < 1078; v1 += 4) {
            byte v7 = ((byte)v2);
            v0[v1 + 2] = v7;
            v0[v1 + 1] = v7;
            v0[v1] = v7;
            v0[v1 + 3] = 0;
            ++v2;
        }

        System.arraycopy(v0, 0, v3, 0, v0.length);
        System.arraycopy(imput, 0, v3, 1078, col * row);
        return v3;
    }
}

