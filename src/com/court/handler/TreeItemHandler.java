/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.court.handler;

import com.court.db.HibernateUtil;
import com.court.model.UserRole;
import java.io.File;
import java.io.IOException;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Shanaka P
 */
public class TreeItemHandler extends DefaultHandler {

    private TreeItem<ItemProp> item = new CheckBoxTreeItem<>();
    private int roleId;
    private UserRole uRole;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    @Override
    public void startDocument() throws SAXException {
        setUserRole(getRoleId());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        // finish this node by going back to the parent
        this.item = this.item.getParent();
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        // start a new node and use it as the current item
        ItemProp itemProp = new ItemProp(
                Integer.parseInt(attributes.getValue("id")), attributes.getValue("text"));

        if (uRole != null
                && !uRole.getPrivCats().isEmpty()) {
            uRole.getPrivCats().forEach(e -> {
                if (Integer.parseInt(attributes.getValue("id")) == e.getUsrRolePrivilage().getPrivId()) {
                    itemProp.setIsSelect(true);
                }
            });
        }

        CheckBoxTreeItem<ItemProp> item = new CheckBoxTreeItem<>(itemProp);
        item.setSelected(itemProp.isIsSelect());
        this.item.getChildren().add(item);
        this.item = item;
    }

    public CheckBoxTreeItem<ItemProp> readData(File file) throws SAXException,
            ParserConfigurationException, IOException {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        SAXParser parser = parserFactory.newSAXParser();
        XMLReader reader = parser.getXMLReader();

        // parse file using the content handler to create a TreeItem representation
        reader.setContentHandler(this);
        reader.parse(file.toURI().toString());
        // use first child as root (the TreeItem initially created does not contain data from the file)
        TreeItem<ItemProp> item = this.item.getChildren().get(0);
        this.item.getChildren().clear();
        return (CheckBoxTreeItem) item;
    }

    private void setUserRole(int user_role) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Criteria c = session.createCriteria(UserRole.class);
        UserRole role = (UserRole) c.add(Restrictions.eq("id", user_role)).uniqueResult();
        this.uRole = role;
        session.close();
    }

    public static class ItemProp {

        private int id;
        private boolean isSelect;
        private String text;

        public ItemProp() {
        }

        public ItemProp(int id, boolean isSelect, String text) {
            this.id = id;
            this.isSelect = isSelect;
            this.text = text;
        }

        public ItemProp(int id, String text) {
            this.id = id;
            this.text = text;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public boolean isIsSelect() {
            return isSelect;
        }

        public void setIsSelect(boolean isSelect) {
            this.isSelect = isSelect;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return this.text;
        }

    }

}
