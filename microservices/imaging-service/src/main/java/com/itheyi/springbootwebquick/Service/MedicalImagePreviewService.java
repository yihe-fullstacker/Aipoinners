package com.itheyi.springbootwebquick.Service;

import org.springframework.stereotype.Service;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

@Service
public class MedicalImagePreviewService {

    private static final long PREVIEW_TRIGGER_FILE_SIZE = 700 * 1024L;
    private static final int MAX_PREVIEW_DIMENSION = 1600;
    private static final float JPEG_QUALITY = 0.82f;

    public void preparePreviewIfNeeded(File originalFile, String filename) {
        try {
            resolveDisplayFile(originalFile, filename);
        } catch (Exception ignored) {
        }
    }

    public File resolveDisplayFile(File originalFile, String filename) throws IOException {
        if (originalFile == null || !originalFile.exists() || !isPreviewable(filename)) {
            return originalFile;
        }

        File previewFile = getPreviewFile(originalFile);
        if (previewFile.exists() && previewFile.lastModified() >= originalFile.lastModified()) {
            return previewFile;
        }

        BufferedImage source = ImageIO.read(originalFile);
        if (source == null) {
            return originalFile;
        }

        int width = source.getWidth();
        int height = source.getHeight();
        boolean needsResize = Math.max(width, height) > MAX_PREVIEW_DIMENSION;
        boolean needsPreview = needsResize || originalFile.length() > PREVIEW_TRIGGER_FILE_SIZE;
        if (!needsPreview) {
            return originalFile;
        }

        BufferedImage outputImage = resize(source, width, height);
        writeJpeg(outputImage, previewFile);
        return previewFile.exists() ? previewFile : originalFile;
    }

    private boolean isPreviewable(String filename) {
        if (filename == null) {
            return false;
        }
        String lowerName = filename.toLowerCase();
        return lowerName.endsWith(".jpg")
                || lowerName.endsWith(".jpeg")
                || lowerName.endsWith(".png")
                || lowerName.endsWith(".bmp");
    }

    private File getPreviewFile(File originalFile) {
        return new File(originalFile.getParentFile(), originalFile.getName() + ".preview.jpg");
    }

    private BufferedImage resize(BufferedImage source, int width, int height) {
        double scale = Math.min(1.0, (double) MAX_PREVIEW_DIMENSION / Math.max(width, height));
        int targetWidth = Math.max(1, (int) Math.round(width * scale));
        int targetHeight = Math.max(1, (int) Math.round(height * scale));

        if (targetWidth == width && targetHeight == height && source.getType() == BufferedImage.TYPE_INT_RGB) {
            return source;
        }

        BufferedImage output = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = output.createGraphics();
        try {
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.drawImage(source, 0, 0, targetWidth, targetHeight, null);
        } finally {
            graphics.dispose();
        }
        return output;
    }

    private void writeJpeg(BufferedImage image, File outputFile) throws IOException {
        File parent = outputFile.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            ImageIO.write(image, "jpg", outputFile);
            return;
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(outputFile)) {
            writer.setOutput(outputStream);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            if (writeParam.canWriteCompressed()) {
                writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                writeParam.setCompressionQuality(JPEG_QUALITY);
            }
            writer.write(null, new IIOImage(image, null, null), writeParam);
        } finally {
            writer.dispose();
        }
    }
}
