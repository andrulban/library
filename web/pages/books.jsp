<%@page import="jbeans.RequestParser"%>
<%@page import="java.util.Enumeration"%>
<%@page import="enums.SearchType"%>
<%@page import="java.util.ArrayList"%>
<%@page import="jbeans.Book"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>

<%@include file="/WEB-INF/jspf/side_bar.jspf" %>
<%@include file="/WEB-INF/jspf/letters.jspf" %>
<div class="book_list">

    <jsp:useBean id="bookList" class="jbeans.BookList" scope="page"/>
    <jsp:useBean id="requestParser" class="jbeans.RequestParser" scope="page"/>
    <%
        request.setCharacterEncoding("UTF-8");
        long genreId = 0;
        long pageNumber = 0;
        ArrayList<Book> list = null;
        boolean isFirst = false;

        //Parameters getting block
        if (request.getParameter("page") == null || request.getParameter("page").equals("0")
                || session.getAttribute("AmountOfAvailableBooks") == null) { //it is first page
            isFirst = true;
        }
        try {
            if (request.getParameter("page") != null) {
                pageNumber = Long.valueOf(request.getParameter("page"));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Content and pages regenrating block
        list = requestParser.requestParsBooksList(request, isFirst, pageNumber);

        long availableBooks = (Long) session.getAttribute("AmountOfAvailableBooks");
        long maxPages = 0;
        long module = availableBooks % 6;
        maxPages = (availableBooks - module) / 6;

        if (module > 0) {
            availableBooks -= module;
            maxPages++;
        }
        if (pageNumber > maxPages) {
            pageNumber = maxPages - 1;
            list = requestParser.requestParsBooksList(request, isFirst, pageNumber);
        }
    %>
    <h3>${param.name}</h3>
    <h5 style="text-align: left; margin-top:20px;">Найдено книг: <%=session.getAttribute("AmountOfAvailableBooks")%> </h5>
    <%  session.setAttribute("currentBookList", list);
        for (Book book : list) {

    %>

    <div class="book_info">
        <div class="book_title">
            <p> <%=book.getName()%></p>
        </div>
        <div class="book_image">
            <img src="<%=request.getContextPath()%>/ImageShower?index=<%=list.indexOf(book)%>" height="250" width="190" alt="Обложка"/>
        </div>
        <div class="book_details">
            <br><strong>ISBN:</strong> <%=book.getIsbn()%>
            <br><strong>Издательство:</strong> <%=book.getPublisher()%>

            <br><strong>Количество страниц:</strong> <%=book.getPageCount()%>
            <br><strong>Год издания:</strong> <%=book.getPublishDate()%>
            <br><strong>Автор:</strong> <%=book.getAuthor()%>
            <p style="margin:10px;"> <a href="/03_Library_1/pages/content.jsp?index=<%=list.indexOf(book)%>">Читать</a></p>
        </div>
    </div>


    <%}%>
</div>
<div class="numbers">
    <%
        String style = "";
        ArrayList<String> listOfRequestParameterNames = new ArrayList<String>();
        StringBuilder paramersAndTheirValues = new StringBuilder();
        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String parName = parameterNames.nextElement();
            if (!parName.equals("page")) {
                paramersAndTheirValues.append(parName);
                paramersAndTheirValues.append("=");
                paramersAndTheirValues.append(request.getParameter(parName));
                paramersAndTheirValues.append("&");
            }
        }
        if (pageNumber <= 9) {
            for (long i = 0; i < maxPages; i++) {
                if (i == pageNumber) {
                    style = "style=\"color:red;\"";
                } else {
                    style = "";
                }
    %>
    <a <%=style%> href="books.jsp?<%=paramersAndTheirValues%>page=<%=i%>"><%=i%></a>
    <%}
    } else if (maxPages > pageNumber) {
        for (long i = pageNumber - 8; i < pageNumber + 2; i++) {
            if (i == pageNumber) {
                style = "style=\"color:red;\"";
            } else {
                style = "";
            }%>
    <a <%=style%> href="books.jsp?<%=paramersAndTheirValues%>page=<%=i%>"><%=i%></a>
    <%}
        }%>

</div>
