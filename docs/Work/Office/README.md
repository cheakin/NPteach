# Office操作

## 合并PDF

*依赖于`spire.office`*

```java
**
 * PDF工具
 * Create by CK on 2022/01/25.
 **/
public class PDFUtils {

    /**
     * 合并文件到PDF种
     * word/excel/image/pdf
     *
     * @param srcFullPathList
     * @param destFullPath
     * @return
     * @throws IOException
     */

    public static int mergeToPdf(List<String> srcFullPathList, String destFullPath) throws IOException {
        File destFile = new File(destFullPath);
        destFile.getParentFile().mkdirs();
        destFile.createNewFile();
        if (FileUtils.getExtension(destFullPath).indexOf("pdf") < 0) {
            return -1;
        }
        int pages = 0;
        PdfDocument destPdf = new PdfDocument();

        File tempFile = File.createTempFile(PKUtils.uuid(), ".pdf");
        try {
            for (String src : srcFullPathList) {
                String extension = FileUtils.getExtension(src);
                if (extension.indexOf("doc") >= 0) {
                    //实例化Document类的对象
                    Document doc = new Document();
                    //实例化Document类的对象
                    doc.loadFromFile(src);
                    //保存为PDF格式
                    doc.saveToFile(tempFile.getAbsolutePath(), FileFormat.PDF);
                    doc.close();
                }

                if (extension.indexOf("xls") >= 0) {
                    //创建一个Workbook实例并加载Excel文件
                    Workbook workbook = new Workbook();
                    workbook.loadFromFile(src);
                    //设置转换后的PDF页面高宽适应工作表的内容大小
                    workbook.getConverterSetting().setSheetFitToPage(true);
                    //将生成的文档保存到指定路径
                    workbook.saveToFile(tempFile.getAbsolutePath(), com.spire.xls.FileFormat.PDF);
                }

                if (extension.indexOf("png") >= 0 || extension.indexOf("jpg") >= 0) {
                    //创建文档
                    PdfDocument pdf = new PdfDocument();
                    //添加一页
                    PdfPageBase page = pdf.getPages().add();
                    //加载图片，并获取图片高宽
                    PdfImage image = PdfImage.fromFile(src);
                    int width = image.getWidth() / 2;
                    int height = image.getHeight() / 2;
                    //绘制图片到PDF
                    page.getCanvas().drawImage(image, 50, 50, width, height);
                    //保存文档
                    pdf.saveToFile(tempFile.getAbsolutePath());
                    pdf.dispose();
                }

                if (extension.indexOf("pdf") >= 0) {
                    FileUtil.copy(src, tempFile.getAbsolutePath(), true);
                }

                PdfDocument tempPdf = new PdfDocument();
                tempPdf.loadFromFile(tempFile.getAbsolutePath());

                destPdf.appendPage(tempPdf);

//                tempPdf.close();  // 不要不要不要!!!
            }
            destPdf.saveToFile(destFile.getAbsolutePath());
            pages = destPdf.getPages().getCount();
            destPdf.close();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
        return pages;
    }

}
```

> 参考：[JAVA 合并 PDF 文档 (e-iceblue.cn)](https://www.e-iceblue.cn/pdf_java_document_operation/merge-pdf-documents-in-java.html)

