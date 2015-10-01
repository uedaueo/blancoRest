/*
 * blanco Framework
 * Copyright (C) 2004-2009 IGA Tosiki
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 */
package blanco.rest.task;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.TransformerException;

import blanco.rest.BlancoRestConstants;
import blanco.rest.BlancoRestMeta2Xml;
import blanco.rest.BlancoRestObjectsInfo;
import blanco.rest.BlancoRestXml2SourceFile;
import blanco.rest.resourcebundle.BlancoRestResourceBundle;
import blanco.rest.task.valueobject.BlancoRestProcessInput;

//import blanco.rest.BlancoRestXml2SourceFile;


public class BlancoRestProcessImpl implements
        BlancoRestProcess {
    /**
     * このプロダクトのリソースバンドルへのアクセスオブジェクト。
     */
    private final BlancoRestResourceBundle fBundle = new BlancoRestResourceBundle();

    /**
     * {@inheritDoc}
     */
    public int execute(final BlancoRestProcessInput input) {
        System.out.println("- " + BlancoRestConstants.PRODUCT_NAME
                + " (" + BlancoRestConstants.VERSION + ")");

        try {
            final File fileMetadir = new File(input.getMetadir());
            if (fileMetadir.exists() == false) {
                throw new IllegalArgumentException(fBundle
                        .getAnttaskErr001(input.getMetadir()));
            }

            /*
             * validator を作る時に使うために，
             * ValueObject で既に定義されている（はずの）オブジェクトを取得しておく
             */
            final BlancoRestObjectsInfo objectsInfo = new BlancoRestObjectsInfo();
            objectsInfo.setEncoding(input.getEncoding());
            objectsInfo.process(input);

            // テンポラリディレクトリを作成。
            new File(input.getTmpdir()
                    + BlancoRestConstants.TARGET_SUBDIRECTORY)
                    .mkdirs();

            // 指定されたメタディレクトリを処理します。
            new BlancoRestMeta2Xml()
                    .processDirectory(fileMetadir, input.getTmpdir()
                            + BlancoRestConstants.TARGET_SUBDIRECTORY);

            // XML化された中間ファイルからソースコードを生成
            final File[] fileMeta2 = new File(input.getTmpdir()
                    + BlancoRestConstants.TARGET_SUBDIRECTORY)
                    .listFiles();
            for (int index = 0; index < fileMeta2.length; index++) {
                if (fileMeta2[index].getName().endsWith(".xml") == false) {
                    continue;
                }

                final BlancoRestXml2SourceFile xml2source = new BlancoRestXml2SourceFile();
                xml2source.setEncoding(input.getEncoding());
                xml2source.process(fileMeta2[index], "true".equals(input
                        .getNameAdjust()), new File(input.getTargetdir()));
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException(ex.toString());
        } catch (TransformerException ex) {
            throw new IllegalArgumentException(ex.toString());
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            throw ex;
        }
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    public boolean progress(final String argProgressMessage) {
        System.out.println(argProgressMessage);
        return false;
    }
}
