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
		<spring:message
			code="label.management.webservices.updateWebServiceServerConfiguration" />
		<small></small>
	</h1>
</div>

<%-- NAVIGATION --%>
<div class="well well-sm" style="display: inline-block">
	<span class="glyphicon glyphicon-arrow-left" aria-hidden="true"></span>&nbsp;<a
		class=""
		href="${pageContext.request.contextPath}/webservices/management/webservicesservers/webserviceserverconfiguration/"><spring:message
			code="label.event.back" /></a> |&nbsp;&nbsp;
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

<form method="post" class="form-horizontal">
	<div class="panel panel-default">
		<div class="panel-body">
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message code="label.WebServiceServerConfiguration.active" />
				</div>

				<div class="col-sm-2">
					<select id="webServiceServerConfiguration_active" name="active"
						class="form-control">
						<option value="false"><spring:message code="label.no" /></option>
						<option value="true"><spring:message code="label.yes" /></option>
					</select>
					<script>
		$("#webServiceServerConfiguration_active").val('<c:out value='${not empty param.active ? param.active : webServiceServerConfiguration.active }'/>');
	</script>
				</div>
			</div>
			<div class="form-group row">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.WebServiceServerConfiguration.authenticationLevel" />
				</div>

				<div class="col-sm-4">
					<select id="webServiceServerConfiguration_authenticationLevel"
						class="form-control" name="authenticationlevel">
						<c:forEach items="${authenticationLevelValues}" var="field">
							<option value='<c:out value='${field}'/>'><c:out
									value='${field}' /></option>
						</c:forEach>
					</select>
					<script>
		$("#webServiceServerConfiguration_authenticationLevel").val('<c:out value='${not empty param.authenticationlevel ? param.authenticationlevel : webServiceServerConfiguration.authenticationLevel }'/>');
	</script>
				</div>
			</div>
			<div class="form-group row withWSSecurity" style="display: none;">
				<div class="col-sm-2 control-label">
					<spring:message code="label.WebServiceServerConfiguration.domainKeyStore" />
				</div>

				<div class="col-sm-4">
					<%-- Relation to side 1 drop down rendered in input --%>
					<select id="webServiceServerConfiguration_domainKeyStore"
						class="js-example-basic-single" name="domainkeystore">
					</select>
				</div>
			</div>
			<div class="form-group row withWSSecurity" style="display: none;">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.WebServiceServerConfiguration.aliasForPrivateKey" />
				</div>

				<div class="col-sm-10">
					<select id="webServiceServerConfiguration_aliasForPrivateKey" class="form-control" name="aliasforprivatekey">
					</select>
				</div>
			</div>
			<div class="form-group row withWSSecurity" style="display: none;">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.WebServiceServerConfiguration.passwordForPrivateKey" />
				</div>

				<div class="col-sm-10">
					<input id="webServiceServerConfiguration_passwordForPrivateKey"
						class="form-control" type="password" name="passwordforprivatekey"
						value='<c:out value='${not empty param.passwordforprivatekey ? param.passwordforprivatekey : webServiceServerConfiguration.passwordForPrivateKey }'/>' />
				</div>
			</div>
			<div class="form-group row withAuth" style="display: none;">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.WebServiceServerConfiguration.serviceUsername" />
				</div>

				<div class="col-sm-10">
					<input id="webServiceServerConfiguration_serviceUsername"
						class="form-control" type="text" name="serviceusername"
						value='<c:out value='${not empty param.serviceusername ? param.serviceusername : webServiceServerConfiguration.serviceUsername }'/>' />
				</div>
			</div>
			<div class="form-group row withAuth" style="display: none;">
				<div class="col-sm-2 control-label">
					<spring:message
						code="label.WebServiceServerConfiguration.servicePassword" />
				</div>

				<div class="col-sm-10">
					<input id="webServiceServerConfiguration_servicePassword"
						class="form-control" type="password" name="servicepassword"
						value='<c:out value='${not empty param.servicepassword ? param.servicepassword : webServiceServerConfiguration.servicePassword }'/>' />
				</div>
			</div>
		</div>
		<div class="panel-footer">
			<input type="submit" class="btn btn-default" role="button"
				value="<spring:message code="label.submit" />" />
		</div>
	</div>
</form>

<script>
$(document).ready(function() {

	<%-- Block for providing domainKeyStore options --%>
	domainKeyStore_options = [
		<c:forEach items="${WebServiceServerConfiguration_domainKeyStore_options}" var="element"> 
			{
				text : "<c:out value='${element.name}'/>", 
				id : "<c:out value='${element.externalId}'/>"
			},
		</c:forEach>
	];
	
	$("#webServiceServerConfiguration_domainKeyStore").select2(
		{
			data : domainKeyStore_options,
		}	  
		    );
		    
		    
		    $("#webServiceServerConfiguration_domainKeyStore").select2().select2('val', '<c:out value='${not empty param.domainkeystore ? param.domainkeystore : webServiceServerConfiguration.domainKeyStore.externalId }'/>');
		    <%-- End block for providing domainKeyStore options --%>
	
	
	});

	function enableFields() {
		var value = $("#webServiceServerConfiguration_authenticationLevel").val();
		if (value == "NONE") {
			$(".withAuth").hide();
			$(".withWSSecurity").hide();
			$(".withAuth").attr('value','');
			$(".withPassword").attr('value','');
		} else if (value == "BASIC_AUTH") {
			$(".withAuth").show();
			$(".withWSSecurity").hide();
		} else if (value == "BASIC_AUTH_CUSTOM") {
			$(".withAuth").hide();
			$(".withWSSecurity").hide();
			$(".withAuth").attr('value','');
		}
		else if (value == "WS_SECURITY") {
			$(".withAuth").show();
			$(".withWSSecurity").show();
		}
		else if (value == "WS_SECURITY_CUSTOM") {
			$(".withAuth").hide();
			$(".withWSSecurity").show();
			$(".withAuth").attr('value','');
		}
	}
	
	function requestKeys() {
		var selectedID = $("#webServiceServerConfiguration_domainKeyStore").val();
		var selectedKeyAlias = "${not empty param.aliasforprivatekey ? param.aliasforprivatekey : webServiceServerConfiguration.aliasForPrivateKey }";
		$.getJSON("${pageContext.request.contextPath}/webservices/management/webservicesservers/webserviceserverconfiguration/update/${webServiceServerConfiguration.externalId}/entries/" + selectedID,
			function (data) {
				 $("#webServiceServerConfiguration_aliasForPrivateKey").empty();
				 $.each(data, function( key, val ) {
					 if (val == selectedKeyAlias) {
					 	$("#webServiceServerConfiguration_aliasForPrivateKey").append("<option value='" + val + "' selected>" + val + "</option>")
					 }else {
						 $("#webServiceServerConfiguration_aliasForPrivateKey").append("<option value='" + val + "'>" + val + "</option>")
					 }
					 
				});
					 
			}
		);
	}
	
	$(document).ready(function() {
		enableFields();
		requestKeys();
		$("#webServiceServerConfiguration_authenticationLevel").change(function() {
			enableFields();	
		});
		$("#webServiceServerConfiguration_domainKeyStore").change(function() {
			requestKeys();
		});
	});
</script>
