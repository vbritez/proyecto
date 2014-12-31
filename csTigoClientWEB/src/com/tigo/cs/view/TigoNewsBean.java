package com.tigo.cs.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.tigo.cs.commons.web.view.SMBaseBean;
import com.tigo.cs.domain.Image;
import com.tigo.cs.domain.New;
import com.tigo.cs.facade.NewFacade;

/**
 * 
 * @author exttnu
 * @version $Id$
 */
@ManagedBean(name = "tigoNewsBean")
@ViewScoped
public class TigoNewsBean extends SMBaseBean {

    private static final long serialVersionUID = -7776228684842785501L;
    @EJB
    private NewFacade newFacade;

    private final Integer NEWS_SIZE = 15;
    
    private List<New> newsList;
    private New selectedNew;   
    private int newsCurrentQuantity = NEWS_SIZE;
    private Integer newsTotalCount;
    private boolean loadMore = false;
        
    @PostConstruct
    public void postConstruct(){
        newsTotalCount = newFacade.count(null);
        if(newsTotalCount > newsCurrentQuantity)
            loadMore = true;
    }
    
    public boolean isLoadMore() {
        return loadMore;
    }

    public void setLoadMore(boolean loadMore) {
        this.loadMore = loadMore;
    }

    public Integer getNewsCount() {
        return newsTotalCount;
    }

    public void setNewsCount(Integer newsCount) {
        this.newsTotalCount = newsCount;
    }

    public int getNewsQuantity() {
        return newsCurrentQuantity;
    }

    public void setNewsQuantity(int newsQuantity) {
        this.newsCurrentQuantity = newsQuantity;
    }

    public New getSelectedNew() {
        return selectedNew;
    }

    public void setSelectedNew(New selectedNew) {
        this.selectedNew = selectedNew;
    }

    public List<New> getNewsList() {
        if (newsList == null) {
            newsList = newFacade.getNewsList(newsCurrentQuantity);
        }
        return newsList;
    }

    public void setNewsList(List<New> newsList) {
        this.newsList = newsList;
    }

    public StreamedContent obtainImage(New news) {
        StreamedContent dbImage = null;
        
        List<Image> imageList = newFacade.findImagesByNewCod(news.getNewCod());
        if (imageList != null && imageList.size() > 0){
            InputStream dbStream = new ByteArrayInputStream(imageList.get(0).getSmallImageByt());
            dbImage = new DefaultStreamedContent(dbStream, "image/png");
        }
        // Get inputstream of a blob eg
        // javax.sql.Blob.getInputStream() API
        return dbImage;
    } 

    public String viewBodyNews(New news){
        selectedNew = news;        
        return null;
    }
    
    public String back(){
        selectedNew = null;        
        return null;
    }
    
    public String loadMore(){
        newsCurrentQuantity += NEWS_SIZE;   
        newsList = null;
        getNewsList();
        if(newsTotalCount <= newsCurrentQuantity)
            loadMore = false;
        return null;
    }
}

