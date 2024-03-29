
package controller;

import dao.SarkiDocumentDao;
import entity.SarkiDocument;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Part;

@Named
@SessionScoped
public class SarkiDocmentControllers extends PagintionControllers implements Serializable,Validator{
    private SarkiDocument docment;
    private SarkiDocumentDao docDao;
    private List<SarkiDocument> docList;
    private Part part;
    // bu yolu degistiriniz uploadlar calismasi icin 
    private final String uploadTo ="C:/Users/furka/OneDrive/Masaüstü/muzikLand/musics/";
    
    @Inject
    private SarkiControllers sarkicontrol;
    
    private int sayfaSize = 8;
    private int sayfaAdet = 3;
    private int sonuc;
    private String eror_mesage = null;
    public SarkiDocmentControllers() {
        setSayfa_Adet(sayfaAdet);
        setSayfa_Size(sayfaSize);
        setDao(this.getDocDao());
        sonuc = -1;
    }
    
    public void upload(){
        try {
            if(this.getPart() != null){
            InputStream input = this.getPart().getInputStream();
            File file = new File(uploadTo+part.getSubmittedFileName());             
            Files.copy(input,file.toPath(),StandardCopyOption.REPLACE_EXISTING);
            SimpleDateFormat uploadTime = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
                String d = uploadTime.format(new Date());
                String newName = d + "&&" + file.getName();
                File newFile = new File(uploadTo + newName);
                file.renameTo(newFile);
            this.docment = this.getDocment();            
            docment.setFileName(newName);
            docment.setFile_size(getPart().getSize());
            docment.setFilePath(file.getParent());
            docment.setFileType(part.getContentType());            
            sonuc =  this.getDocDao().insert(docment); 
            this.docment = new SarkiDocument();
            this.sarki_reset();
            }
        } catch (IOException ex) {
            
        }
    }
    public boolean size(SarkiDocument doc){
        if(doc.getFile_size() < 3000000)
            return true;
        else
            return false;
    }
    public void sarki_reset(){
        this.setPart(null);
        this.setEror_mesage(null);
    }
    public void updatePath(){       
        try {               
            if(this.getPart() != null){
            InputStream input = this.part.getInputStream();
            File file = new File(uploadTo+this.part.getSubmittedFileName());             
            Files.copy(input,file.toPath(),StandardCopyOption.REPLACE_EXISTING);
            SimpleDateFormat uploadTime = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss");
                String d = uploadTime.format(new Date());
                String newName = d + "&&" + file.getName();
                File newFile = new File(uploadTo + newName);
                file.renameTo(newFile);
            docment.setFileName(newName);
            docment.setFile_size(getPart().getSize());
            docment.setFilePath(file.getParent());
            docment.setFileType(this.part.getContentType());
            sonuc =  this.getDocDao().update(docment);
            this.docment = new SarkiDocument();  
            this.sarki_reset();
            }
        } catch (IOException ex) {
            
        }
    }
    public void back(){
        this.docment = new SarkiDocument();
        this.sonuc = -1 ;
        this.sarki_reset();
    }
    public void editForm(SarkiDocument sarkiDoc){
        this.docment = sarkiDoc;
        this.sarki_reset();
        this.sonuc = -1;
    }
    public void update(){
        if(this.getEror_mesage() == null){
            if(this.getPart() != null)
                this.updatePath();
            else{
        sarkicontrol.updateForm(this.getDocment().getSarki());
       sarkicontrol.update();       
       this.sonuc = sarkicontrol.getSonuc();
       this.docment = new SarkiDocument(); 
       this.sarki_reset();
            }
        }
    }
    public void delete(){
        this.sonuc = this.getDocDao().delete(this.getDocment());
        this.docment = new SarkiDocument(); 
        this.sarki_reset();
        
    }
     public List<SarkiDocument> getDocList() {
         this.docList = this.getDocDao().findAll(getPage(),getSayfa_Size(),getSearchTerimi(),getId());
        return docList;
    }

    public SarkiDocument getDocment() {
        if(this.docment == null)
            this.docment = new SarkiDocument();
        return docment;
    }


    public SarkiDocumentDao getDocDao() {
        if(this.docDao == null)
            this.docDao = new SarkiDocumentDao();
        return docDao;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public Part getPart() {
        return part;
    }

    public void setEror_mesage(String eror_mesage) {
        this.eror_mesage = eror_mesage;
    }

    public String getEror_mesage() {
        return eror_mesage;
    }
    
    public  String getUploadTo() {
        return uploadTo;
    }

    @Override
    public void validate(FacesContext arg0, UIComponent arg1, Object arg2) throws ValidatorException {            
        Part p = (Part)arg2;
        if(p == null)
            this.setEror_mesage("Lutfen bir dosya seciniz..");
            //throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"lutfen bir dosya seciniz","lutfen bir dosya seciniz"));       
        else if(p.getSize() > 20971520 ){
            this.setEror_mesage("max boyut 20 MB");
          //  throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"max boyut 20 MB","max boyut 20 MB"));
        
        }else if(!"audio/mp3".equals(p.getContentType()) && !"video/mp4".equals(p.getContentType())&& !"video/3gpp".equals(p.getContentType()) ){
            this.setEror_mesage("gecersiz bir dosya uzantisi yuklediniz");
          //  throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR,"gecersiz bir dosya uzantisi yuklediniz","gecersiz bir dosya uzantisi yuklediniz"));      
        }
    }

    public SarkiControllers getSarkicontrol() {
        return sarkicontrol;
    }

    public void setSonuc(int sonuc) {
        this.sonuc = sonuc;
    }

    public int getSonuc() {
        return sonuc;
    }

    
    
}
