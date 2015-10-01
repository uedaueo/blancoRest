package blanco.rest;

import blanco.commons.util.BlancoStringUtil;
import blanco.rest.task.valueobject.BlancoRestProcessInput;
import blanco.valueobject.BlancoValueObjectConstants;
import blanco.valueobject.resourcebundle.BlancoValueObjectResourceBundle;
import blanco.valueobject.valueobject.BlancoValueObjectClassStructure;
import blanco.valueobject.valueobject.BlancoValueObjectFieldStructure;
import blanco.xml.bind.BlancoXmlBindingUtil;
import blanco.xml.bind.BlancoXmlUnmarshaller;
import blanco.xml.bind.valueobject.BlancoXmlDocument;
import blanco.xml.bind.valueobject.BlancoXmlElement;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * BlancoValueObject で作成されているObjectの一覧を XML から取得し，保持しておきます
 *
 * Created by tueda on 15/07/05.
 */
public class BlancoRestObjectsInfo {
    /**
     * ValueObject 用リソースバンドルへのアクセスオブジェクト。
     */
    private final BlancoValueObjectResourceBundle fBundle = new BlancoValueObjectResourceBundle();

    /**
     * フィールド名やメソッド名の名前変形を行うかどうか。
     */
    private boolean fNameAdjust = true;

    /**
     * 自動生成するソースファイルの文字エンコーディング。
     */
    private String fEncoding = null;

    public void setEncoding(final String argEncoding) {
        fEncoding = argEncoding;
    }

    public static HashMap<String, BlancoValueObjectClassStructure> objects = new HashMap<>();

    public void process(final BlancoRestProcessInput input) throws IOException {

        // XML化された中間ファイルから情報を読み込む
        final File[] fileMeta3 = new File(input.getTmpdir()
                + BlancoValueObjectConstants.TARGET_SUBDIRECTORY)
                .listFiles();
        for (int index = 0; index < fileMeta3.length; index++) {
            if (fileMeta3[index].getName().endsWith(".xml") == false) {
                continue;
            }

            process(fileMeta3[index], "true".equals(input
                    .getNameAdjust()), new File(input.getTargetdir()));
        }
    }

    /**
     * 中間XMLファイルからソースコードを自動生成します。
     *
     * @param argMetaXmlSourceFile
     *            メタ情報が含まれているXMLファイル。
     * @param argDirectoryTarget
     *            ソースコード生成先ディレクトリ (/mainを除く部分を指定します)。
     * @param argNameAdjust
     *            名前変形を行うかどうか。
     * @throws IOException
     *             入出力例外が発生した場合。
     */
    public void process(final File argMetaXmlSourceFile,
                        final boolean argNameAdjust, final File argDirectoryTarget)
            throws IOException {
        fNameAdjust = argNameAdjust;

        // メタ情報を解析してバリューオブジェクトのツリーを取得します。
        final BlancoXmlDocument documentMeta = new BlancoXmlUnmarshaller()
                .unmarshal(argMetaXmlSourceFile);

        // ルートエレメントを取得します。
        final BlancoXmlElement elementRoot = BlancoXmlBindingUtil
                .getDocumentElement(documentMeta);
        if (elementRoot == null) {
            // ルートエレメントが無い場合には処理中断します。
            return;
        }

        // sheet(Excelシート)のリストを取得します。
        final List<BlancoXmlElement> listSheet = BlancoXmlBindingUtil
                .getElementsByTagName(elementRoot, "sheet");
        final int sizeListSheet = listSheet.size();
        for (int index = 0; index < sizeListSheet; index++) {
            // おのおののシートを処理します。
            final BlancoXmlElement elementSheet = (BlancoXmlElement) listSheet
                    .get(index);

            // 共通情報を取得します。
            final BlancoXmlElement elementCommon = BlancoXmlBindingUtil
                    .getElement(elementSheet, fBundle
                            .getMeta2xmlElementCommon());
            if (elementCommon == null) {
                // commonが無い場合には、このシートの処理をスキップします。
                continue;
            }

            final String name = BlancoXmlBindingUtil.getTextContent(
                    elementCommon, "name");
            if (BlancoStringUtil.null2Blank(name).trim().length() == 0) {
                // nameが空の場合には処理をスキップします。
                continue;
            }

            // 一覧情報を取得します。
            final BlancoXmlElement elementList = BlancoXmlBindingUtil
                    .getElement(elementSheet, fBundle.getMeta2xmlElementList());

            // シートから詳細な情報を取得します。
            final BlancoValueObjectClassStructure processStructure = parseSheet(
                    elementCommon, elementList, argDirectoryTarget);

            if (processStructure != null) {
                objects.put(name, processStructure);
            }
        }
    }

    /**
     * sheetエレメントを展開します。
     *
     * @param argElementCommon
     *            現在処理しているCommonノード。
     * @param argElementList
     *            現在処理しているListノード。
     * @param argDirectoryTarget
     *            ソースコードの出力先フォルダ。
     * @return 収集されたメタ情報構造データ。
     */
    private BlancoValueObjectClassStructure parseSheet(
            final BlancoXmlElement argElementCommon,
            final BlancoXmlElement argElementList, final File argDirectoryTarget) {

        final BlancoValueObjectClassStructure processStructure = new BlancoValueObjectClassStructure();
        processStructure.setName(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "name"));
        processStructure.setPackage(BlancoXmlBindingUtil.getTextContent(
                argElementCommon, "package"));

        if (BlancoStringUtil.null2Blank(processStructure.getPackage()).trim()
                .length() == 0) {
            throw new IllegalArgumentException(fBundle
                    .getXml2sourceFileErr001(processStructure.getName()));
        }

        if (BlancoXmlBindingUtil
                .getTextContent(argElementCommon, "description") != null) {
            processStructure.setDescription(BlancoXmlBindingUtil
                    .getTextContent(argElementCommon, "description"));
        }

        if (BlancoXmlBindingUtil.getTextContent(argElementCommon,
                "fileDescription") != null) {
            processStructure.setFileDescription(BlancoXmlBindingUtil
                    .getTextContent(argElementCommon, "fileDescription"));
        }

        if (argElementList == null) {
            return null;
        }

        // 一覧の内容を取得します。
        final List<BlancoXmlElement> listField = BlancoXmlBindingUtil
                .getElementsByTagName(argElementList, "field");
        for (int indexField = 0; indexField < listField.size(); indexField++) {
            final Object nodeField = listField.get(indexField);

            if (nodeField instanceof BlancoXmlElement == false) {
                continue;
            }

            final BlancoXmlElement elementField = (BlancoXmlElement) nodeField;
            BlancoValueObjectFieldStructure field = new BlancoValueObjectFieldStructure();
            field
                    .setNo(BlancoXmlBindingUtil.getTextContent(elementField,
                            "no"));

            field.setName(BlancoXmlBindingUtil.getTextContent(elementField,
                    "name"));
            if (BlancoStringUtil.null2Blank(field.getName()).length() == 0) {
                continue;
            }

            // 既に同じ内容が登録されていないかどうかのチェック。
            for (int indexPast = 0; indexPast < processStructure.getFieldList()
                    .size(); indexPast++) {
                final BlancoValueObjectFieldStructure fieldPast = processStructure
                        .getFieldList().get(indexPast);
                if (fieldPast.getName().equals(field.getName())) {
                    throw new IllegalArgumentException(
                            fBundle.getXml2sourceFileErr003(processStructure
                                    .getName(), field.getName()));
                }
            }

            field.setType(BlancoXmlBindingUtil.getTextContent(elementField,
                    "type"));
            if (BlancoStringUtil.null2Blank(field.getType()).length() == 0) {
                // ここで異常終了。
                continue;
            }

            field.setDefault(BlancoXmlBindingUtil.getTextContent(elementField,
                    "default"));

            field.setDescription(BlancoXmlBindingUtil.getTextContent(
                    elementField, "description"));

            processStructure.getFieldList().add(field);
        }

        return processStructure;
    }
}
