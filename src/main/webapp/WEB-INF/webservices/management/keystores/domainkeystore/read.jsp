<%-- 
 * This file was created by Quorum Born IT <http://www.qub-it.com/> and its 
 * copyright terms are bind to the legal agreement regulating the FenixEdu@ULisboa 
 * software development project between Quorum Born IT and Serviços Partilhados da
 * Universidade de Lisboa:
 *  - Copyright © 2015 Quorum Born IT (until any Go-Live phase)
 *  - Copyright © 2015 Universidade de Lisboa (after any Go-Live phase)
 *
 * Contributors: paulo.abrantes@qub-it.com
 *
 * 
 * This file is part of FenixEdu bennu-webservices.
 *
 * FenixEdu bennu-webservices is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu bennu-webservices is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu bennu-webservices.  If not, see <http://www.gnu.org/licenses/>.
 --%>
 
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt"%>
<spring:url var="datatablesUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.latest.min.js" />
<spring:url var="datatablesBootstrapJsUrl"
	value="/javaScript/dataTables/media/js/jquery.dataTables.bootstrap.min.js"></spring:url>
<script type="text/javascript" src="${datatablesUrl}"></script>
<script type="text/javascript" src="${datatablesBootstrapJsUrl}"></script>
<spring:url var="datatablesCssUrl"
	value="/CSS/dataTables/dataTables.bootstrap.min.css" />
<link rel="stylesheet" href="${datatablesCssUrl}" />
<spring:url var="datatablesI18NUrl"
	value="/javaScript/dataTables/media/i18n/${portal.locale.language}.json" />

<link rel="stylesheet" type="text/css"
	href="${pageContext.request.contextPath}/CSS/dataTables/dataTables.bootstrap.min.css" />

<link
	href="//cdn.datatables.net/responsive/1.0.4/css/dataTables.responsive.css"
	rel="stylesheet" />
<script
	src="//cdn.datatables.net/responsive/1.0.4/js/dataTables.responsive.js"></script>
<link
	href="//cdn.datatables.net/tabletools/2.2.3/css/dataTables.tableTools.css"
	rel="stylesheet" />
<script
	src="//cdn.datatables.net/tabletools/2.2.3/js/dataTables.tableTools.min.js"></script>
<link
	href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/css/select2.min.css"
	rel="stylesheet" />
<script
	src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-rc.1/js/select2.min.js"></script>

<!-- Choose ONLY ONE:  bennuToolkit OR bennuAngularToolkit -->
<%--${portal.angularToolkit()} --%>
${portal.toolkit()}

<%-- TITLE --%>
<div class="page-header">
	<h1>
		<spring:message code="label.management.keystores.readDomainKeyStore" />
		<small></small>
	</h1>
</div>
<div class="modal fade" id="deleteModal">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<button type="button" class="close" data-dismiss="modal"
					aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
				<h4 class="modal-title">
					<spring:message code="label.confirmation" />
				</h4>
			</div>
			<div class="modal-body">
				<p>Tem a certeza que deseja apagar ?</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-default" data-dismiss="modal">
					<spring:message code="label.close" />
				</button>
				<a class="btn btn-danger"
					href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/delete/${domainKeyStore.externalId}">
					<spring:message code="label.delete" />
				</a>
			</div>
		</div>
		<!-- /.modal-content -->
	</div>
	<!-- /.modal-dialog -->
</div>
<!-- /.modal -->
<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/"><span
		class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.back" /></a> |&nbsp;&nbsp; <a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/update/${domainKeyStore.externalId}"><span
		class="glyphicon glyphicon-pencil" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.update" /></a> |&nbsp;&nbsp; <a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/read/${domainKeyStore.externalId}/downloadkeystore"><span
		class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.management.keystores.downloadKeystore" /></a>
	|&nbsp;&nbsp; <a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/read/${domainKeyStore.externalId}/uploadkeystore"><span
		class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.management.keystores.uploadKeystore" /></a>
	|&nbsp;&nbsp; <a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/read/${domainKeyStore.externalId}/addcertificate"><span
		class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.management.keystores.addCertificate" /></a>
	|&nbsp;&nbsp; <a class=""
		href="${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/read/${domainKeyStore.externalId}/addkey"><span
		class="glyphicon glyphicon-cog" aria-hidden="true"></span>&nbsp;<spring:message
			code="label.event.management.keystores.addKey" /></a>
</div>
<c:if test="${not empty infoMessages}">
	<div class="alert alert-info" role="alert">

		<c:forEach items="${infoMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty warningMessages}">
	<div class="alert alert-warning" role="alert">

		<c:forEach items="${warningMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>
<c:if test="${not empty errorMessages}">
	<div class="alert alert-danger" role="alert">

		<c:forEach items="${errorMessages}" var="message">
			<p>${message}</p>
		</c:forEach>

	</div>
</c:if>

<div class="panel panel-primary">
	<div class="panel-heading">
		<h3 class="panel-title">
			<spring:message code="label.details" />
		</h3>
	</div>
	<div class="panel-body">
		<form method="post" class="form-horizontal">
			<table class="table">
				<tbody>
					<tr>
						<th scope="row" class="col-xs-3"><spring:message
								code="label.DomainKeyStore.name" /></th>
						<td><c:out value='${domainKeyStore.name}' /></td>
					</tr>
				</tbody>
			</table>
		</form>
	</div>
</div>



<h2>Entries</h2>

<c:choose>
	<c:when test="${not empty domainKeyStore.helper.entries}">
		<table id="entriesTable"  class="table responsive table-bordered table-hover">
			<thead>
				<tr>
					<th>Alias</th>
					<th>Valid From</th>
					<th>Valid to</th>
					<th></th>
				</tr>
			</thead>
			<tbody>
			</tbody>
		</table>
	</c:when>
	<c:otherwise>
		<div class="alert alert-info" role="alert">

			<spring:message code="label.noResultsFound" />

		</div>

	</c:otherwise>
</c:choose>

<script>
	var entriesSet = [
			<c:forEach items="${domainKeyStore.helper.entries}" var="entry">
				{
				"alias" : "<c:out value='${entry.alias}'/>",
				"validFrom" : "<c:out value='${entry.validFrom}'/>",
				"validTo" : "<c:out value='${entry.validTo}'/>",
				"actions" : "<a  class=\"btn btn-danger btn-xs\" href=\"${pageContext.request.contextPath}/webservices/management/keystores/domainkeystore/deleteEntry/${domainKeyStore.externalId}/${entry.alias}\"><spring:message code='label.delete'/></a>" },
            </c:forEach>
    ];
	
	$(document).ready(function() {
	
		var table = $('#entriesTable').DataTable({language : {
			url : "${datatablesI18NUrl}",			
		},
		"columns": [
			{ data: 'alias' },
			{ data: 'validFrom' },
			{ data: 'validTo' },
			{ data: 'actions' }
			
		],
		"data" : entriesSet
		});
		table.columns.adjust().draw();
	}); 
</script>
