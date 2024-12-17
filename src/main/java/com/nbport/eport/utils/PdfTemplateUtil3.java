package com.nbport.eport.utils;
 
 
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
 
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
 
public class PdfTemplateUtil3 implements Runnable {
    private Map<String, Object> data;
    private String templateFileName;
    private String fileName;
    private HttpServletResponse response;
    @Resource
    private PdfTemplateUtil3 pdfTemplate;
 
    public PdfTemplateUtil3(Map<String, Object> data, String templateFileName, String fileName, HttpServletResponse response) {
        this.data = data;
        this.templateFileName = templateFileName;
        this.fileName = fileName;
        this.response = response;
    }
 
    @Override
    public void run() {
        try {
            ByteArrayOutputStream baos = pdfTemplate.createPDF(data, templateFileName);
            response.setContentType("application/x-msdownload");
            // 告诉浏览器，当前响应数据要求用户干预保存到文件中，以及文件名是什么 如果文件名有中文，必须URL编码
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            try {
                baos.writeTo(response.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
 
    }
 
    public ByteArrayOutputStream createPDF(Map<String, Object> data, String templateFileName) throws Exception {
        // 创建一个FreeMarker实例, 负责管理FreeMarker模板的Configuration实例
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // 指定FreeMarker模板文件的位置
        configuration.setClassForTemplateLoading(PdfTemplateUtil3.class, "/templates");
        ITextRenderer renderer = new ITextRenderer();
        OutputStream out = new ByteArrayOutputStream();
        StringWriter writer = new StringWriter();
        try {
            // 设置 css中 的字体样式（暂时仅支持宋体和黑体） 必须，不然中文不显示
            ITextFontResolver fontResolver = renderer.getFontResolver();
            fontResolver.addFont("C:/Windows/Fonts/simsun.ttc", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont("C:/Windows/Fonts/simhei.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            fontResolver.addFont("C:/Windows/Fonts/simkai.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
 
            // 设置模板的编码格式
            configuration.setEncoding(Locale.CHINA, "UTF-8");
            // 获取模板文件
            Template template = configuration.getTemplate(templateFileName, "UTF-8");
            // 将数据输出到html中
            template.process(data, writer);
            writer.flush();
            String html = writer.toString();
            // 把html代码传入渲染器中
            renderer.setDocumentFromString(html);
 
            // 设置模板中的图片路径 （这里的images在resources目录下） 模板中img标签src路径需要相对路径加图片名 如<img src="images/xh.jpg"/>
            String url = PdfTemplateUtil3.class.getClassLoader().getResource("static/images").toURI().toString();
            renderer.getSharedContext().setBaseURL(url);
            renderer.layout();
 
            renderer.createPDF(out, false);
            renderer.finishPDF();
            out.flush();
            return (ByteArrayOutputStream) out;
        } finally {
            if (writer != null) {
                writer.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}