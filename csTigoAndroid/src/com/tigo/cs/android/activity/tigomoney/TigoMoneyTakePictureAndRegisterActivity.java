package com.tigo.cs.android.activity.tigomoney;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.SessionEntity;
import com.tigo.cs.android.service.LocationService;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.TigoMoneyPhotoService;
import com.tigo.cs.api.entities.TigoMoneyService;

public class TigoMoneyTakePictureAndRegisterActivity extends AbstractActivity {

    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 1777;
    private static final int MAX_FACES = 1;

    private TigoMoneyTmpEntity currentEntity;
    private boolean takeFrontPhotoBool = false;
    private boolean takeBackPhotoBool = false;

    private final static String imagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)/*
                                                                                                                      * Environment
                                                                                                                      * .
                                                                                                                      * getExternalStorageDirectory
                                                                                                                      * (
                                                                                                                      * )
                                                                                                                      */
        + File.separator + "TigoMoney" + File.separator; // + "DCIM" +
                                                         // File.separator +
                                                         // "Camera" +
                                                         // File.separator;

    static {
        deleteAllFilesInTigoMoneyDir();
    }

    @Override
    public Integer getServicecod() {
        return -3;
    }

    @Override
    public String getServiceEventCod() {
        return "REGISTER";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tigo_money_register_photo_main);
        onSetContentViewFinish();

        initializeContent();

        showFrontPhotoButton();
        showBackPhotoButton();
    }

    private static void deleteAllFilesInTigoMoneyDir() {
        /* Borrar todas las fotos que estan en la carpeta de TigoMoney */
        File dir = new File(imagePath);
        if (dir.isDirectory() && dir.list() != null && dir.list().length > 0) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        dir.delete();
        // File dir = new File(imagePath);
        // if (dir.isDirectory() && dir.list() != null && dir.list().length > 0)
        // {
        // String[] children = dir.list();
        // for (int i = 0; i < children.length; i++) {
        // if (children[i].startsWith("front")){
        // new File(dir, children[i]).delete();
        // }
        // }
        // }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeContent();
    }

    private void initializeContent() {

        currentEntity = CsTigoApplication.getCurrentTigoMoneyEntity();
        if (currentEntity != null && currentEntity.getFrontPhoto() != null) {
            showFrontPhotoImageView(currentEntity.getFrontPhoto());
        }

        if (currentEntity != null && currentEntity.getBackPhoto() != null) {
            showBackPhotoImageView(currentEntity.getBackPhoto());
        }

        File folder = new File(imagePath);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        if (currentEntity != null
            && currentEntity.getFrontPhotoFileName() == null) {
            String frontImagePath = imagePath + "frontImage"
                + format.format(date) + ".jpg";
            currentEntity.setFrontPhotoFileName(frontImagePath);
        }

        if (currentEntity != null
            && currentEntity.getBackPhotoFileName() == null) {
            String backImagePath = imagePath + "backImage"
                + format.format(date) + ".jpg";
            currentEntity.setBackPhotoFileName(backImagePath);
        }

        CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);

        Button backButton = (Button) findViewById(R.id.tigoMoneyBackwardsButton);
        backButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startEventActivity(TigoMoneyRegisterActivity.class);
            }
        });

        Button registerButton = (Button) findViewById(R.id.tigoMoneyRegisterButton);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new TigoMoneyService();

                if (!startUserMark()) {
                    return;
                }

                SessionEntity session = CsTigoApplication.getSessionHelper().findLast();

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), "2",
                        currentEntity.getIdentification(),
                        String.valueOf(currentEntity.getBirthDate().getTime()),
                        currentEntity.getAddress(), currentEntity.getCity(),
                        session.getCellphoneNumber());

                ((TigoMoneyService) entity).setRegistrationChannel("1");
                ((TigoMoneyService) entity).setBirthDate(currentEntity.getBirthDate().getTime());
                ((TigoMoneyService) entity).setIdentification(currentEntity.getIdentification());
                ((TigoMoneyService) entity).setAddress(currentEntity.getAddress());
                ((TigoMoneyService) entity).setCity(currentEntity.getCity());
                ((TigoMoneyService) entity).setSource(session.getCellphoneNumber());

                TigoMoneyPhotoService photo = new TigoMoneyPhotoService();
                photo.setFrontPhoto(convertBitmapToByteArray(
                        currentEntity.getFrontPhotoFileName(),
                        currentEntity.getFrontPhoto()));
                photo.setBackPhoto(convertBitmapToByteArray(
                        currentEntity.getBackPhotoFileName(),
                        currentEntity.getBackPhoto()));
                ((TigoMoneyService) entity).setPhotoEntity(photo.toString());

                ((TigoMoneyService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((TigoMoneyService) entity).setRequiresLocation(false);

                deletePhoto(currentEntity.getFrontPhotoFileName());
                deletePhoto(currentEntity.getBackPhotoFileName());
                deleteAllFilesInTigoMoneyDir();

                endUserMark(msg);

            }
        });

    }

    private void showFrontPhotoButton() {
        LinearLayout tigoMoneyLayout = (LinearLayout) findViewById(R.id.tigomoneyFrontPhotolayout);
        tigoMoneyLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout;

        layout = (RelativeLayout) li.inflate(
                R.layout.tigo_money_front_photo_button_subcontent, null);
        Button takeFrontPhotoButton = (Button) layout.findViewById(R.id.takeFrontPhotoButton);
        takeFrontPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentEntity.setTakeFrontPhotoBool(true);
                CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);

                takeFrontPhotoBool = true;
                openCamera();
            }
        });

        tigoMoneyLayout.addView(layout);
    }

    private void showFrontPhotoImageView(Bitmap image) {
        LinearLayout tigoMoneyLayout = (LinearLayout) findViewById(R.id.tigomoneyFrontPhotolayout);
        tigoMoneyLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout;

        layout = (RelativeLayout) li.inflate(
                R.layout.tigo_money_front_photo_view_subcontent, null);
        Button deleteButton = (Button) layout.findViewById(R.id.deleteFrontPhoto);
        deleteButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentEntity != null) {
                    currentEntity.setFrontPhoto(null);
                    CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);
                }
                showFrontPhotoButton();
            }
        });

        ImageView imageView = (ImageView) layout.findViewById(R.id.frontPhotoView);
        imageView.setImageBitmap(image);

        tigoMoneyLayout.addView(layout);
    }

    private void showBackPhotoButton() {
        LinearLayout tigoMoneyLayout = (LinearLayout) findViewById(R.id.tigomoneyBackPhotolayout);
        tigoMoneyLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout;

        layout = (RelativeLayout) li.inflate(
                R.layout.tigo_money_back_photo_button_subcontent, null);
        Button takeBackPhotoButton = (Button) layout.findViewById(R.id.takeBackPhotoButton);
        takeBackPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                currentEntity.setTakeBackPhotoBool(true);
                CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);

                takeBackPhotoBool = true;
                openCamera();
            }
        });

        tigoMoneyLayout.addView(layout);
    }

    private void showBackPhotoImageView(Bitmap image) {
        LinearLayout tigoMoneyLayout = (LinearLayout) findViewById(R.id.tigomoneyBackPhotolayout);
        tigoMoneyLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout;

        layout = (RelativeLayout) li.inflate(
                R.layout.tigo_money_back_photo_view_subcontent, null);
        Button deleteBackButton = (Button) layout.findViewById(R.id.deleteBackPhoto);
        deleteBackButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (currentEntity != null) {
                    currentEntity.setBackPhoto(null);
                    CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);
                }
                showBackPhotoButton();
            }
        });

        ImageView imageView = (ImageView) layout.findViewById(R.id.backPhotoView);
        imageView.setImageBitmap(image);

        tigoMoneyLayout.addView(layout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE
            && resultCode == -1) {

            String path = "";
            if (currentEntity.isTakeFrontPhotoBool()) {
                path = currentEntity.getFrontPhotoFileName();
            } else if (currentEntity.isTakeBackPhotoBool()) {
                path = currentEntity.getBackPhotoFileName();
            }

            File file = new File(path);
            Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(),
                    1000, 700);

            if (currentEntity.isTakeFrontPhotoBool()) {
                currentEntity.setFrontPhoto(bitmap);
                showFrontPhotoImageView(bitmap);
                currentEntity.setTakeFrontPhotoBool(false);
                takeFrontPhotoBool = false;
            } else if (currentEntity.isTakeBackPhotoBool()) {
                currentEntity.setBackPhoto(bitmap);
                showBackPhotoImageView(bitmap);
                currentEntity.setTakeBackPhotoBool(false);
                takeBackPhotoBool = false;
            }

            CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);
            deleteGaleryImages();
        }
    }

    private void deleteGaleryImages() {
        final String[] imageColumns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.SIZE, MediaStore.Images.Media._ID };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        final String imageWhere = MediaStore.Images.Media._ID + ">?";
        final String[] imageArguments = { Integer.toString(currentEntity.getLastId()) };
        Cursor imageCursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
                imageWhere, imageArguments, imageOrderBy);
        try {
            if (imageCursor.getCount() >= 1) {
                while (imageCursor.moveToNext()) {
                    int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
                    String path = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Long takenTimeStamp = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
                    Long size = imageCursor.getLong(imageCursor.getColumnIndex(MediaStore.Images.Media.SIZE));
                    // if(path.contentEquals(TigoMoneyTakePictureAndRegisterActivity.this.capturePath)){
                    // Remove it
                    ContentResolver cr = getContentResolver();
                    cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            MediaStore.Images.Media._ID + "=?",
                            new String[] { Long.toString(id) });
                    break;
                    // }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deletePhoto(String fileName) {
        File file = new File(fileName);
        file.delete();
    }

    public byte[] convertBitmapToByteArray(String filePath, Bitmap bitmap) {
        byte[] b = null;
        try {
            ByteArrayOutputStream ou = new ByteArrayOutputStream();

            // String filepath2 = Environment.getExternalStorageDirectory() +
            // File.separator + "DCIM" + File.separator + "Camera" +
            // File.separator
            // + "/facedetect" + System.currentTimeMillis() + ".jpg";

            // FileOutputStream fos = new FileOutputStream(filepath2);

            bitmap.compress(CompressFormat.JPEG, 60, ou);
            // bitmap.compress(CompressFormat.JPEG, 10, ou);

            b = ou.toByteArray();

            ou.flush();
            ou.close();

            return b;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    public Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        final int height = options.outHeight;
        final int width = options.outWidth;

        reqWidth = 400;// (width * 15) / 100;
        reqHeight = 300;// (height * 15) / 100;

        // options.inPreferredConfig = Bitmap.Config.RGB_565;
        // int inSampleSize = 1;
        //
        // if (height > reqHeight) {
        // inSampleSize = Math.round((float) height / (float) reqHeight);
        // }
        // int expectedWidth = width / inSampleSize;
        //
        // if (expectedWidth > reqWidth) {
        // inSampleSize = Math.round((float) width / (float) reqWidth);
        // }

        // options.inSampleSize = inSampleSize;
        // options.inJustDecodeBounds = false;

        float scaleWidth = ((float) reqWidth) / width;
        float scaleHeight = ((float) reqHeight) / height;
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bm = BitmapFactory.decodeFile(path);

        // RECREATE THE NEW BITMAP
        Bitmap toReturn = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                false);

        // Bitmap toReturn = BitmapFactory.decodeFile(path, options);
        return toGrayscale(toReturn);

    }

    public Bitmap toGrayscale(Bitmap bmpOriginal) {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        final Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(bmpGrayscale);
        final Paint paint = new Paint();
        // final ColorMatrix cm = new ColorMatrix();
        // cm.setSaturation(0);
        float[] mat = new float[] {
                0.3f,
                0.59f,
                0.11f,
                0,
                0,
                0.3f,
                0.59f,
                0.11f,
                0,
                0,
                0.3f,
                0.59f,
                0.11f,
                0,
                0,
                0,
                0,
                0,
                1,
                0, };
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(mat);
        paint.setColorFilter(filter);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    /************************************************************/
    private int getLastImageId() {
        final String[] imageColumns = { MediaStore.Images.Media._ID };
        final String imageOrderBy = MediaStore.Images.Media._ID + " DESC";
        final String imageWhere = null;
        final String[] imageArguments = null;
        Cursor imageCursor = managedQuery(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageColumns,
                imageWhere, imageArguments, imageOrderBy);
        if (imageCursor.moveToFirst()) {
            int id = imageCursor.getInt(imageCursor.getColumnIndex(MediaStore.Images.Media._ID));
            // imageCursor.close();
            return id;
        } else {
            return 0;
        }
    }

    /************************************************************/

    private void openCamera() {

        currentEntity.setLastId(getLastImageId());

        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        String path = "";
        if (takeFrontPhotoBool) {
            path = currentEntity.getFrontPhotoFileName();
        } else if (takeBackPhotoBool) {
            path = currentEntity.getBackPhotoFileName();
        }

        File file = new File(path);
        file.setReadable(true, false);
        file.setWritable(true, false);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent,
                CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }

    private boolean detectFrontFaces() {
        if (currentEntity.getFrontPhoto() != null) {
            int width = currentEntity.getFrontPhoto().getWidth();
            int height = currentEntity.getFrontPhoto().getHeight();

            FaceDetector detector = new FaceDetector(width, height, TigoMoneyTakePictureAndRegisterActivity.MAX_FACES);
            Face[] faces = new Face[TigoMoneyTakePictureAndRegisterActivity.MAX_FACES];

            // Boolean isGrayScale = checkIfGrayscale(cameraBitmap);

            Bitmap bitmap565 = Bitmap.createBitmap(width, height,
                    Config.RGB_565);
            Paint ditherPaint = new Paint();
            Paint drawPaint = new Paint();

            ditherPaint.setDither(true);
            drawPaint.setColor(Color.RED);
            drawPaint.setStyle(Paint.Style.STROKE);
            drawPaint.setStrokeWidth(2);

            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap565);
            canvas.drawBitmap(currentEntity.getFrontPhoto(), 0, 0, ditherPaint);

            int facesFound = detector.findFaces(bitmap565, faces);
            PointF midPoint = new PointF();
            float eyeDistance = 0.0f;
            float confidence = 0.0f;

            Log.i("FaceDetector", "Number of faces found: " + facesFound);

            if (facesFound > 0) {
                for (int index = 0; index < facesFound; ++index) {
                    faces[index].getMidPoint(midPoint);
                    eyeDistance = faces[index].eyesDistance();
                    confidence = faces[index].confidence();

                    Log.i("FaceDetector", "Confidence: " + confidence
                        + ", Eye distance: " + eyeDistance + ", Mid Point: ("
                        + midPoint.x + ", " + midPoint.y + ")");

                    canvas.drawRect((int) midPoint.x - eyeDistance,
                            (int) midPoint.y - eyeDistance, (int) midPoint.x
                                + eyeDistance, (int) midPoint.y + eyeDistance,
                            drawPaint);
                }
                return true;
            } else {
                return false;
            }

            // String filepath = Environment.getExternalStorageDirectory() +
            // File.separator + "DCIM" + File.separator + "Camera" +
            // File.separator
            // + "/facedetect" + System.currentTimeMillis() + ".jpg";
            //
            // try {
            // FileOutputStream fos = new FileOutputStream(filepath);
            //
            // bitmap565.compress(CompressFormat.JPEG, 60, fos);
            //
            // fos.flush();
            // fos.close();
            // } catch (FileNotFoundException e) {
            // e.printStackTrace();
            // } catch (IOException e) {
            // e.printStackTrace();
            // }

            // ImageView imageView = (ImageView) findViewById(R.id.image_view);
            //
            // imageView.setImageBitmap(bitmap565);
        }
        return false;
    }

    boolean isGrayScalePixel(int pixel) {
        int red = Color.red(pixel);
        int green = Color.green(pixel);
        int blue = Color.blue(pixel);
        int alpha = Color.alpha(pixel);
        // int alpha = (pixel & 0xFF000000) >> 24;
        // int red = (pixel & 0x00FF0000) >> 16;
        // int green = (pixel & 0x0000FF00) >> 8;
        // int blue = (pixel & 0x000000FF);

        if (0 == alpha && red == green && green == blue) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    protected boolean validateUserInput() {
        if (currentEntity.getFrontPhoto() == null) {
            Toast.makeText(
                    TigoMoneyTakePictureAndRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_front_photo_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (!detectFrontFaces()) {
            Toast.makeText(
                    TigoMoneyTakePictureAndRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_can_not_detect_faces),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (currentEntity.getBackPhoto() == null) {
            Toast.makeText(
                    TigoMoneyTakePictureAndRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_back_photo_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void endUserMark(String message) {

        Notifier.info(getClass(),
                "Se persistira el mensaje antes en la base de datos interna.");
        Long messageId = persistMessage(message);

        Notifier.info(getClass(), "Mensaje persistido en la base de datos");

        Message msg = Message.obtain(null, 0, new ClientHandler());

        Notifier.info(getClass(),
                "Se obtiene mensajero para el envio de mensajes al servicio.");
        try {

            MessageEntity persistedMessage = CsTigoApplication.getMessageHelper().find(
                    messageId);
            persistedMessage.setMessage(persistedMessage.getMessage() + "%*"
                + messageId);
            CsTigoApplication.getMessageHelper().update(persistedMessage);

            entity.setMessageId(messageId);

            msg.obj = entity;
            msg.what = LocationService.LOCATE_AND_SEND;

            Notifier.info(getClass(),
                    "Seteados los parametros, se enviara el mensaje al servicio.");
            messenger.send(msg);

            Notifier.info(getClass(), "Mensaje enviado al servicio.");

            Toast.makeText(CsTigoApplication.getContext(),
                    R.string.sending_message, Toast.LENGTH_LONG).show();

        } catch (RemoteException e) {
            e.printStackTrace();
        }

        goToMain();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        deleteAllFilesInTigoMoneyDir();
    }

}