package com.nbport.eport.vo;
 
 
public class PdfDataTest {
    private Integer column1;
 
    private String column2;
 
    private String column3;
 
    private String column4;
 
    private String column5;
 
    public PdfDataTest(Integer column1, String column2, String column3, String column4, String column5) {
        this.column1 = column1;
        this.column2 = column2;
        this.column3 = column3;
        this.column4 = column4;
        this.column5 = column5;
    }
 
    public PdfDataTest() {
    }
 
    public Integer getColumn1() {
        return column1;
    }
 
    public void setColumn1(Integer column1) {
        this.column1 = column1;
    }
 
    public String getColumn2() {
        return column2;
    }
 
    public void setColumn2(String column2) {
        this.column2 = column2;
    }
 
    public String getColumn3() {
        return column3;
    }
 
    public void setColumn3(String column3) {
        this.column3 = column3;
    }
 
    public String getColumn4() {
        return column4;
    }
 
    public void setColumn4(String column4) {
        this.column4 = column4;
    }
 
    public String getColumn5() {
        return column5;
    }
 
    public void setColumn5(String column5) {
        this.column5 = column5;
    }
 
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PdfDataTest{");
        sb.append("column1=").append(column1);
        sb.append(", column2='").append(column2).append('\'');
        sb.append(", column3='").append(column3).append('\'');
        sb.append(", column4='").append(column4).append('\'');
        sb.append(", column5='").append(column5).append('\'');
        sb.append('}');
        return sb.toString();
    }
}