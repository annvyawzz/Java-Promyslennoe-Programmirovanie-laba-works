package com.example.springbootxml.service;

import com.example.springbootxml.model.Book;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class XMLService {

    private static final String XML_PATH = "src/main/resources/xml/library.xml";
    private final XPath xPath;

    public XMLService() {
        XPathFactory xPathFactory = XPathFactory.newInstance();
        this.xPath = xPathFactory.newXPath();
    }

    public List<Book> loadBooks() {
        List<Book> books = new ArrayList<>();

        try {
            Document doc = parseXML();
            NodeList nodeList = (NodeList) xPath.compile("/library/book").evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element bookElement = (Element) nodeList.item(i);
                Book book = new Book();

                book.setId(Integer.parseInt(bookElement.getAttribute("id")));
                book.setCopies(Integer.parseInt(bookElement.getAttribute("copies")));
                book.setAvailable(Integer.parseInt(bookElement.getAttribute("available")));
                book.setTitle(getElementText(bookElement, "title"));
                book.setAuthor(getElementText(bookElement, "author"));
                book.setYear(Integer.parseInt(getElementText(bookElement, "year")));
                book.setPrice(Double.parseDouble(getElementText(bookElement, "price")));
                book.setCategory(getElementText(bookElement, "category"));

                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    // Поиск по XPath
    public List<Book> searchByXPath(String xpathExpression) {
        List<Book> books = new ArrayList<>();

        try {
            Document doc = parseXML();
            NodeList nodeList = (NodeList) xPath.compile(xpathExpression).evaluate(doc, XPathConstants.NODESET);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Element bookElement = (Element) nodeList.item(i);
                Book book = new Book();

                book.setId(Integer.parseInt(bookElement.getAttribute("id")));
                book.setCopies(Integer.parseInt(bookElement.getAttribute("copies")));
                book.setAvailable(Integer.parseInt(bookElement.getAttribute("available")));
                book.setTitle(getElementText(bookElement, "title"));
                book.setAuthor(getElementText(bookElement, "author"));
                book.setYear(Integer.parseInt(getElementText(bookElement, "year")));
                book.setPrice(Double.parseDouble(getElementText(bookElement, "price")));
                book.setCategory(getElementText(bookElement, "category"));

                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    // XPath запросы для разных критериев поиска
    public List<Book> searchByAuthor(String author) {
        String xpath = "/library/book[contains(translate(author, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" +
                author.toLowerCase() + "')]";
        return searchByXPath(xpath);
    }

    public List<Book> searchByYear(int year) {
        String xpath = "/library/book[year=" + year + "]";
        return searchByXPath(xpath);
    }

    public List<Book> searchByCategory(String category) {
        String xpath = "/library/book[category='" + category + "']";
        return searchByXPath(xpath);
    }

    public List<Book> searchByTitle(String title) {
        String xpath = "/library/book[contains(translate(title, 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" +
                title.toLowerCase() + "')]";
        return searchByXPath(xpath);
    }

    // Сохранение книг
    public void saveBooks(List<Book> books) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element root = doc.createElement("library");
            doc.appendChild(root);

            for (Book book : books) {
                Element bookElement = doc.createElement("book");
                bookElement.setAttribute("id", String.valueOf(book.getId()));
                bookElement.setAttribute("copies", String.valueOf(book.getCopies()));
                bookElement.setAttribute("available", String.valueOf(book.getAvailable()));

                bookElement.appendChild(createElement(doc, "title", book.getTitle()));
                bookElement.appendChild(createElement(doc, "author", book.getAuthor()));
                bookElement.appendChild(createElement(doc, "year", String.valueOf(book.getYear())));
                bookElement.appendChild(createElement(doc, "price", String.valueOf(book.getPrice())));
                bookElement.appendChild(createElement(doc, "category", book.getCategory()));

                root.appendChild(bookElement);
            }

            // Сохраняем в файл
            saveDocument(doc);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document parseXML() throws Exception {
        File xmlFile = new File(XML_PATH);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private void saveDocument(Document doc) throws Exception {
        javax.xml.transform.TransformerFactory transformerFactory =
                javax.xml.transform.TransformerFactory.newInstance();
        javax.xml.transform.Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        javax.xml.transform.dom.DOMSource source = new javax.xml.transform.dom.DOMSource(doc);
        javax.xml.transform.stream.StreamResult result =
                new javax.xml.transform.stream.StreamResult(new File(XML_PATH));
        transformer.transform(source, result);
    }

    private String getElementText(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            Node node = nodeList.item(0);
            return node.getTextContent();
        }
        return "";
    }

    private Element createElement(Document doc, String name, String value) {
        Element element = doc.createElement(name);
        element.appendChild(doc.createTextNode(value));
        return element;
    }
}