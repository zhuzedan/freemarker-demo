package com.nbport.eport.utils;
 
import com.lowagie.text.pdf.BaseFont;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.xhtmlrenderer.pdf.ITextFontResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
 
/**
 * @Auther: admin
 * @Date: 2019/8/10 16:17
 * @Description: 有返回值的多线程
 */
public class PdfTemplateUtil2 implements Callable {
    private Map<String, Object> data;
    private String templateFileName;
 
    public PdfTemplateUtil2(Map<String, Object> data, String templateFileName) {
        this.data = data;
        this.templateFileName = templateFileName;
    }
 
    @Override
    public Object call() throws Exception {
        return getObject();
    }
 
    private Object getObject() throws IOException {
        Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        // 指定FreeMarker模板文件的位置
        configuration.setClassForTemplateLoading(PdfTemplateUtil2.class, "/templates");
        ITextRenderer renderer = new ITextRenderer();
 
        StringWriter writer = new StringWriter();
        OutputStream out = new ByteArrayOutputStream();
        ITextFontResolver fontResolver = renderer.getFontResolver();
        try {
            // 设置 css中 的字体样式（暂时仅支持宋体和黑体） 必须，不然中文不显示
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
            // String url = PdfTemplateUtil2.class.getClassLoader().getResource("static/images").toURI().toString();
            // renderer.getSharedContext().setBaseURL(url);
            renderer.layout();
            renderer.createPDF(out, false);
            renderer.finishPDF();
            out.flush();
 
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
        return (ByteArrayOutputStream) out;
    }
}
 