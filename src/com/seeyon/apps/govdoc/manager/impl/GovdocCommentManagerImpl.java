package com.seeyon.apps.govdoc.manager.impl;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.seeyon.apps.govdoc.vo.GovdocCommentVO;
import com.seeyon.apps.govdoc.vo.GovdocDealVO;
import com.seeyon.apps.govdoc.vo.GovdocNewVO;
import com.seeyon.apps.govdoc.vo.GovdocSummaryVO;
import com.seeyon.apps.govdoc.vo.MemberPostEdocVO;
import com.seeyon.ctp.organization.OrgConstants;
import com.seeyon.ctp.organization.bo.MemberRole;
import com.seeyon.ctp.organization.bo.V3xOrgAccount;
import com.seeyon.ctp.organization.bo.V3xOrgDepartment;
import com.seeyon.ctp.organization.bo.V3xOrgPost;
import com.seeyon.ctp.organization.bo.V3xOrgLevel;
import com.seeyon.ctp.organization.bo.V3xOrgMember;
import com.seeyon.ctp.organization.bo.MemberPost;
import com.seeyon.ctp.organization.bo.V3xOrgRelationship;
import com.seeyon.ctp.organization.bo.V3xOrgRole;
import com.seeyon.ctp.organization.manager.BusinessOrgManagerDirect;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.seeyon.apps.agent.bo.AgentModel;
import com.seeyon.apps.agent.bo.MemberAgentBean;
import com.seeyon.apps.collaboration.enums.ColHandleType;
import com.seeyon.apps.collaboration.enums.CommentExtAtt1Enum;
import com.seeyon.apps.govdoc.constant.GovdocAppLogAction;
import com.seeyon.apps.govdoc.constant.GovdocConstant;
import com.seeyon.apps.govdoc.constant.GovdocEnum.OperationType;
import com.seeyon.apps.govdoc.event.GovdocEventDispatcher;
import com.seeyon.apps.govdoc.helper.GovdocHelper;
import com.seeyon.apps.govdoc.manager.GovdocAffairManager;
import com.seeyon.apps.govdoc.manager.GovdocCommentManager;
import com.seeyon.apps.govdoc.manager.GovdocContentManager;
import com.seeyon.apps.govdoc.manager.GovdocFormManager;
import com.seeyon.apps.govdoc.manager.GovdocLogManager;
import com.seeyon.apps.govdoc.manager.GovdocOpenManager;
import com.seeyon.apps.govdoc.manager.GovdocPishiManager;
import com.seeyon.apps.govdoc.manager.GovdocPubManager;
import com.seeyon.apps.govdoc.manager.GovdocSignetManager;
import com.seeyon.apps.govdoc.manager.GovdocSummaryManager;
import com.seeyon.apps.govdoc.manager.GovdocWorkflowManager;
import com.seeyon.apps.govdoc.po.EdocLeaderPishiNo;
import com.seeyon.apps.govdoc.po.EdocUserLeaderRelation;
import com.seeyon.apps.govdoc.po.FormOptionExtend;
import com.seeyon.apps.govdoc.po.FormOptionSort;
import com.seeyon.apps.govdoc.po.GovdocComment;
import com.seeyon.apps.govdoc.util.GovdocParamUtil;
import com.seeyon.apps.index.api.IndexApi;
import com.seeyon.ctp.common.AppContext;
import com.seeyon.ctp.common.ModuleType;
import com.seeyon.ctp.common.SystemEnvironment;
import com.seeyon.ctp.common.affair.enums.AffairExtPropEnums;
import com.seeyon.ctp.common.affair.manager.AffairManager;
import com.seeyon.ctp.common.affair.util.AffairUtil;
import com.seeyon.ctp.common.authenticate.domain.User;
import com.seeyon.ctp.common.config.SystemConfig;
import com.seeyon.ctp.common.constants.ApplicationCategoryEnum;
import com.seeyon.ctp.common.content.ContentConfig;
import com.seeyon.ctp.common.content.ContentInterface;
import com.seeyon.ctp.common.content.comment.Comment;
import com.seeyon.ctp.common.content.comment.Comment.CommentType;
import com.seeyon.ctp.common.content.comment.CommentManager;
import com.seeyon.ctp.common.exceptions.BusinessException;
import com.seeyon.ctp.common.filemanager.manager.AttachmentEditHelper;
import com.seeyon.ctp.common.filemanager.manager.AttachmentManager;
import com.seeyon.ctp.common.filemanager.manager.FileManager;
import com.seeyon.ctp.common.i18n.ResourceUtil;
import com.seeyon.ctp.common.permission.bo.DetailAttitude;
import com.seeyon.ctp.common.permission.manager.PermissionManager;
import com.seeyon.ctp.common.permission.vo.PermissionVO;
import com.seeyon.ctp.common.po.affair.CtpAffair;
import com.seeyon.ctp.common.po.comment.CtpCommentAll;
import com.seeyon.ctp.common.po.filemanager.Attachment;
import com.seeyon.ctp.common.po.filemanager.V3XFile;
import com.seeyon.ctp.common.processlog.ProcessLogAction;
import com.seeyon.ctp.common.processlog.po.ProcessLog;
import com.seeyon.ctp.common.taglibs.functions.Functions;
import com.seeyon.ctp.datasource.annotation.DataSourceName;
import com.seeyon.ctp.datasource.annotation.ProcessInDataSource;
import com.seeyon.ctp.form.api.FormApi4Cap3;
import com.seeyon.ctp.form.bean.FormAuthViewBean;
import com.seeyon.ctp.form.bean.FormAuthViewFieldBean;
import com.seeyon.ctp.form.bean.FormBean;
import com.seeyon.ctp.form.bean.FormDataMasterBean;
import com.seeyon.ctp.form.bean.FormViewBean;
import com.seeyon.ctp.form.modules.engin.formula.FormulaEnums;
import com.seeyon.ctp.organization.dao.OrgHelper;
import com.seeyon.ctp.organization.manager.OrgManager;
import com.seeyon.ctp.util.DBAgent;
import com.seeyon.ctp.util.DateUtil;
import com.seeyon.ctp.util.Datetimes;
import com.seeyon.ctp.util.FlipInfo;
import com.seeyon.ctp.util.JDBCAgent;
import com.seeyon.ctp.util.ParamUtil;
import com.seeyon.ctp.util.Strings;
import com.seeyon.ctp.util.UUIDLong;
import com.seeyon.ctp.util.annotation.AjaxAccess;
import com.seeyon.ctp.util.json.JSONUtil;
import com.seeyon.v3x.edoc.constants.EdocOpinionDisplayEnum.OpinionShowNameTypeEnum;
import com.seeyon.v3x.edoc.dao.EdocOpinionDao;
import com.seeyon.v3x.edoc.domain.EdocOpinion;
import com.seeyon.v3x.edoc.domain.EdocSummary;
import com.seeyon.v3x.edoc.webmodel.FormOpinionConfig;
import com.seeyon.v3x.system.signet.domain.V3xHtmDocumentSignature;
import com.seeyon.v3x.system.signet.domain.V3xSignet;
import com.seeyon.v3x.system.signet.enums.V3xHtmSignatureEnum;

/**
 * 公文意见管理类(公文单中及单外)
 * 
 * @author 唐桂林
 */
public class GovdocCommentManagerImpl implements GovdocCommentManager {

	private static final Log LOGGER = LogFactory.getLog(GovdocCommentManagerImpl.class);

	private EdocOpinionDao edocOpinionDao;
	private GovdocSummaryManager govdocSummaryManager;
	private GovdocWorkflowManager govdocWorkflowManager;
	private GovdocContentManager govdocContentManager;
	private GovdocLogManager govdocLogManager;
	private GovdocFormManager govdocFormManager;
	private GovdocSignetManager govdocSignetManager;
	private GovdocOpenManager govdocOpenManager;
	private GovdocPishiManager govdocPishiManager;
	private GovdocPubManager govdocPubManager;
	private GovdocAffairManager govdocAffairManager;
	
	private PermissionManager permissionManager;
	private AffairManager affairManager;
	private CommentManager ctpCommentManager;
	private AttachmentManager attachmentManager;
	private FileManager fileManager;
	private IndexApi indexApi;
	private OrgManager orgManager;
	private FormApi4Cap3 formApi4Cap3;
	private SystemConfig systemConfig;
	private BusinessOrgManagerDirect businessOrgManagerDirect;

	/*************************** 00000 公文单意见查看 start ***************************/
	@Override
	public Map<String, String> getOptionPo(GovdocSummaryVO summaryVO) throws BusinessException {
		Map<String, String> opMap = new HashMap<String, String>();
		/** 意见元素 */
		String nodePolicyFromWorkflow[] = null;
		String nodePermissionPolicy = "";
		// FormOptionSortManager govdocFormOpinionSortManager =
		// (FormOptionSortManager)AppContext.getBean("formOptionSortManager");
		if (null != summaryVO.getActivityId())
			nodePolicyFromWorkflow = govdocWorkflowManager.getNodePolicyIdAndName(ApplicationCategoryEnum.edoc.name(), String.valueOf(summaryVO.getProcessId()),
					String.valueOf(summaryVO.getActivityId()));
		if (null != nodePolicyFromWorkflow)
			nodePermissionPolicy = nodePolicyFromWorkflow[0];
		try {
			String disPosition = "";
			if (null != summaryVO.getSummary()) {
				// disPosition=govdocFormOpinionSortManager.getDisOpsition(summaryVO.getSummary().getFormAppid(),summaryVO.getSummary(),summaryVO.getAffair());
			}
			opMap.put("disPosition", disPosition);
			opMap.put("nodePermissionPolicy", nodePermissionPolicy);

		} catch (Exception exc) {
			LOGGER.error("查询处理意见错误", exc);
		}
		return opMap;
	}

	/*************************** 00000 公文单意见查看 end ***************************/

	/*************************** 11111 公文单意见保存 start ***************************/
	@Override
	public Long updateOpinion2Draft(Long affairId, EdocSummary summary) throws BusinessException {
		List<CtpCommentAll> allDelOpin = ctpCommentManager.getDealOpinion(affairId);
		Long commentId = 0l;
		if (!allDelOpin.isEmpty()) {
			CtpCommentAll all = allDelOpin.get(0);
			all.setCtype(CommentType.draft.getKey());
			Comment comment = new Comment(all);
			ctpCommentManager.updateCommentCtype(comment.getId(), CommentType.draft);
			List<Comment> list = ctpCommentManager.getCommentList(ModuleType.edoc, summary.getId(), all.getId());
			ctpCommentManager.deleteCommentAllByModuleIdAndParentId(ModuleType.edoc, summary.getId(), all.getId());
			if (Strings.isNotEmpty(list)) {
				for (Comment c : list) {
					attachmentManager.deleteByReference(summary.getId(), c.getId());
				}
			}
			// 标记意见为删除状态
			edocOpinionDao.deleteDealOpinionByAffairId(affairId);
		}
		return commentId;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void saveOpinionDraft(Long affairId, Long summaryId) throws BusinessException {
		try {
			Comment comment = new Comment();
			ParamUtil.getJsonDomainToBean("comment_deal", comment);
			comment.setCtype(CommentType.draft.getKey());
			comment.setPushMessage(false);
			Map para = ParamUtil.getJsonDomain("comment_deal");
			if(para.get("content_coll")!= null) {//公文回复意见控件更名
				String content_coll=(String)para.get("content_coll");
	        	comment.setContent(content_coll);
	        }
			if ("1".equals((String) para.get("praiseInput"))) {
				comment.setPraiseToSummary(true);
			}			
			Long commentId = comment.getId();// new 2
			String draftCommentId = (String) para.get("draftCommentId");
			if (Strings.isNotBlank(draftCommentId)) {
				commentId = Long.valueOf((String) para.get("draftCommentId"));// 1
			}
			ctpCommentManager.deleteComment(ModuleType.edoc, commentId);
			attachmentManager.deleteByReference(comment.getModuleId(), comment.getId());
			ctpCommentManager.insertComment(comment);
			
		} catch (Exception e) {
			LOGGER.error("", e);
			throw new BusinessException(e);
		}
	}
	
	
	@Override
	public void saveOpinionPo(EdocOpinion po) throws BusinessException {
		this.edocOpinionDao.save(po);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void finishOpinion(EdocSummary summary, CtpAffair affair, FormBean fb, Comment comment, String operation, ColHandleType handleType, String chooseOpinionType) {
		try {
			if (null != handleType && handleType == ColHandleType.wait) {
				return;
			}
			HttpServletRequest request = AppContext.getRawRequest();
			Map parm = ParamUtil.getJsonDomain("comment_deal");
			String rightId = (String) parm.get("rightId");
			String distributeAffairId = request.getParameter("distributeAffairId");
			if (Strings.isNotBlank(rightId) || (Strings.isBlank(distributeAffairId) && Strings.isBlank(rightId))) {
				if (Strings.isBlank(rightId)) {
					Map<String, Object> vomMap = new HashMap<String, Object>();
					vomMap.put("formAppid", summary.getFormAppid());
					vomMap.put("govdocType", summary.getGovdocType());
					rightId = formApi4Cap3.getGovdocFormViewRight(vomMap, affair);
				}
				if (Strings.isBlank(rightId)) {
					return;
				}
				String[] viewAndRight = rightId.split("_");
				boolean hasSetDel = false;
				
				FormDataMasterBean formDataMasterBean = formApi4Cap3.findDataById(summary.getFormRecordid(),summary.getFormAppid(),null);
				Map<String, Object> formulaMap = formDataMasterBean.getFormulaMap(FormulaEnums.componentType_condition);

				Map commentDeal = ParamUtil.getJsonDomain("comment_deal");
				String commentPostInfo = GovdocParamUtil.getString(commentDeal, "postInfo");

				for (String string : viewAndRight) {
					String operationId = string;
					if (string.indexOf(".") > -1) {
						operationId = string.split("\\.")[1];
					}
					List<FormAuthViewFieldBean> formAuthViewFieldBeans = new ArrayList<FormAuthViewFieldBean>();
					for (FormViewBean formViewBean : fb.getAllViewList()) {
						for (FormAuthViewBean formAuthViewBean : formViewBean.getAllOperations()) {
							if (formAuthViewBean.getId() == Long.parseLong(operationId)) {
								for (FormAuthViewFieldBean favfb : formAuthViewBean.getFormAuthorizationFieldList()) {
									favfb=formAuthViewBean.getFormAuthorizationField(favfb.getFieldName(), formulaMap);
									if ("edit".equals(favfb.getAccess())) {// 如果可以编辑则保存
										formAuthViewFieldBeans.add(favfb);
									}
								}
							}
						}
					}
					List<FormOptionSort> lists = govdocFormManager.findOptionSortByFormId(fb.getId());
					if (null != lists) {
						// 如果有该值，则选的是被回退者选择覆盖方式
						if (Strings.isNotBlank(chooseOpinionType)) {
							if (Long.parseLong(chooseOpinionType) == 1 && !hasSetDel) {// 被回退者选择的方式为保留最后一次
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("nodeId", affair.getActivityId());
								map.put("edocId", affair.getObjectId());
								map.put("memberId", AppContext.currentUserId());
								edocOpinionDao.bulkUpdate("update EdocOpinion opinion set opinion.state=1 where opinion.state=0 "
								+ " and exists(select 1 from CtpAffair affair where affair.id = opinion.affairId and affair.objectId = :edocId and affair.activityId = :nodeId"
								           + " and affair.memberId = :memberId)" , map);
								hasSetDel = true;
							}
						}
						for (FormOptionSort govdocFormOpinionSort : lists) {
							for (FormAuthViewFieldBean favfb : formAuthViewFieldBeans) {
								if (favfb.getFieldName().equals(govdocFormOpinionSort.getProcessName())) {
									EdocOpinion signOpinion = new EdocOpinion();
									String postInfo = "";
									if ("finish".equals(operation)) {
										signOpinion.setPolicy(favfb.getFieldName());
										setOpinionValue(signOpinion, request, affair, summary, comment);
										postInfo = commentPostInfo;
										signOpinion.setPostInfo(postInfo);
									} else if ("stepBack".equals(operation)) {
										signOpinion.setPolicy(favfb.getFieldName());
										setOpinionValue(signOpinion, request, affair, summary, comment);
										signOpinion.setOpinionType(EdocOpinion.OpinionType.backOpinion.ordinal());
										postInfo = commentPostInfo;
										signOpinion.setPostInfo(postInfo);

									}
									// if
									// ("pishi".equals(orgManager.checkLeaderPishi(AppContext.getCurrentUser().getId(),
									// affair.getMemberId()))) {
									// signOpinion.setCreateUserId(Long.valueOf(affair.getMemberId()));
									// }
									if(Strings.isBlank(postInfo) && affair.getMatchAccountId() != null)
									{
										postInfo = affair.getMatchAccountId() + "|" +affair.getMatchDepartmentId() +  "|" + (affair.getMatchPostId() == null || affair.getMatchPostId() == -1 ? -1:affair.getMatchPostId()) + "|" + (affair.getMatchRoleId() == null || affair.getMatchRoleId() == -1 ? -1:affair.getMatchRoleId());
									}

									if(Strings.isBlank(postInfo) && comment.getAccountId() != null)
									{
										postInfo = comment.getAccountId() + "|" +comment.getDepartmentId() +  "|" + (comment.getPostId() == null || comment.getPostId() == -1 ? -1:comment.getPostId())+ "|" + (comment.getMatchRoleId() == null || comment.getMatchRoleId() == -1 ? -1:comment.getMatchRoleId());
									}


									saveOpinion(signOpinion,affair,postInfo,true);
									//刷新动态表中的字段
									if (handleType != ColHandleType.skipNode) {
										refreshField(fb, summary.getId(), summary.getFormRecordid(),favfb.getFieldName());
									}
								}
							}
						}
					}
				}

			} else {
				// 如果没有rightid 可能是分办
				if (Strings.isNotBlank(distributeAffairId)) {
					List<EdocOpinion> lis = edocOpinionDao.find("from EdocOpinion where affairId =" + distributeAffairId + " and state = 2", null);
					if (lis != null && lis.size() > 0) {
						for (EdocOpinion edocOpinion : lis) {
							edocOpinion.setState(0);
							edocOpinionDao.update(edocOpinion);
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("finishOpinion权限异常", e);
		}
		return;
	}

	@SuppressWarnings("resource")
	private void refreshField(FormBean fb, Long summaryId,Long formRecordId,String fieldName) {
		String tableName = fb.getMasterTableBean().getTableName();
		List<EdocOpinion> opinionList = edocOpinionDao.findEdocOpinionBySummaryId(summaryId, false);
		Map<String,String> fieldValues = new HashMap<String, String>();
		StringBuilder sb = null;
		for (EdocOpinion edocOpinion : opinionList) {
			if(fieldName == null || !fieldName.equals(edocOpinion.getPolicy()))
			{
				continue;
			}
			if(!fieldValues.containsKey(edocOpinion.getPolicy())){
				sb = new StringBuilder();
			}
			if(sb != null ){
				if(Strings.isBlank(edocOpinion.getContent())||edocOpinion.getContent()=="null"){
					sb.append("");
				}else{
					sb.append(edocOpinion.getContent()).append("\n");
				}
				fieldValues.put(edocOpinion.getPolicy(), sb.toString());
			}			
		}
		String sql ="update "+ tableName +" set ";
		List<String> params = new ArrayList<String>();
		for (Map.Entry<String,String> field : fieldValues.entrySet()) {
			sql += field.getKey()+"=?,";
			params.add(field.getValue());
		}
		if(!fieldValues.isEmpty()){
			sql= sql.substring(0,sql.length()-1);
			sql +=" where id=" + formRecordId;
			/* Bug 协同V5OA-192523 公文无法回退 JDBCAgent事务锁问题 去掉参数 JDBCAgent(true) -> JDBCAgent() */
			JDBCAgent jdbcAgent = new JDBCAgent();
			try {
				jdbcAgent.execute(sql, params);
			} catch (Exception e) {
				LOGGER.error("文单意见更新到动态表失败,summaryId:"+summaryId+"/r/n"+e.getMessage());
			}finally {
				if (jdbcAgent != null) {
					jdbcAgent.close();
				}
			}
		}
	}

	private EdocOpinion saveOpinion(EdocOpinion opinion,CtpAffair affair, String postInfo, boolean save2DB){
		if (null == opinion) {
			return null;
		}
		String[] orgInfos = null;
		if(Strings.isNotBlank(postInfo))
		{
			orgInfos = postInfo.split("\\|");
		}

		boolean flag  = true;
		if(orgInfos != null && orgInfos.length >=2)
		{
			try {
				V3xOrgAccount account = orgManager.getAccountById(Long.parseLong(orgInfos[0]));
				if(account != null)
				{
					opinion.setAccountName(account.getShortName());
					V3xOrgDepartment department = orgManager.getDepartmentById(Long.parseLong(orgInfos[1]));
					if(department != null)
					{
						opinion.setDepartmentName(department.getName());
						opinion.setDepartmentSortId(department.getSortId());

						//获取对应的部门岗位职务信息
						setMemberLevelSort(postInfo,affair,opinion);
						flag = false;
					}
				}
			} catch (BusinessException e) {
				LOGGER.error("查询处理意见错误", e);
			}
		}
		if(flag){

			User user = AppContext.getCurrentUser();
			Long departmentId = affair.getMatchDepartmentId();
			Long accountId = affair.getMatchAccountId();
			if(user != null){
				accountId = user.getLoginAccount();
			}
			try {
				V3xOrgDepartment orgDepartment =null;
				if (departmentId != null) {
					if(departmentId != -1L){
						orgDepartment = orgManager.getDepartmentById(departmentId);
					}
				}else{
					V3xOrgMember member = orgManager.getMemberById(affair.getMemberId());
					orgDepartment = orgManager.getDepartmentById(member.getOrgDepartmentId());
				}
				if (null != orgDepartment) {
					opinion.setDepartmentName(orgDepartment.getName());
					opinion.setDepartmentSortId(orgDepartment.getSortId());
				}
				V3xOrgAccount orgAccount=null;
				if (accountId != null ) {
					if(accountId != -1L){
						orgAccount = orgManager.getAccountById(accountId);
					}
				}else{
					V3xOrgMember member = orgManager.getMemberById(affair.getMemberId());
					orgAccount = orgManager.getAccountById(member.getOrgAccountId());
				}
				if (null != orgAccount) {
					opinion.setAccountName(orgAccount.getShortName());
				}
			} catch (BusinessException e) {
				LOGGER.error("保存意见获取部门信息错误：", e);
			}
		}

        if(save2DB) {
            edocOpinionDao.save(opinion); // 保存意见
			this.saveEdocPicSign(opinion);
        }
		return opinion;
	}


	/**
	 * 设置人员和职级的排序号
	 * @param postInfo
	 * @param affair
	 * @param opinion
	 * @throws BusinessException
	 */
	public void setMemberLevelSort(String postInfo, CtpAffair affair, EdocOpinion opinion) throws BusinessException {
		opinion.setPostInfo(postInfo);

		List<MemberPostEdocVO> memberPostVOList = getMeberPostVOList(affair.getMemberId());
		MemberPostEdocVO targetMemberPostVO = null;

		for(MemberPostEdocVO memberPostTemp : memberPostVOList)
		{
			if(memberPostTemp.getIds().contains(postInfo))
			{
				targetMemberPostVO = memberPostTemp;
				break;
			}
		}
		if(targetMemberPostVO != null)
		{
			opinion.setMemberSortId(targetMemberPostVO.getSortId().intValue());
			Long targetLevelId = null;
			V3xOrgLevel level = null;

			Long orgAccountId = targetMemberPostVO.getOrgAccountId();
			V3xOrgAccount account = orgManager.getAccountById(orgAccountId);
			if(account != null){
				opinion.setOrgAccountSortId(account.getSortId().intValue());
			}
			else{
				opinion.setOrgAccountSortId(Integer.MAX_VALUE);
			}

			if(account != null && account.getExternalType() == 4){
				//多维组织没有岗位和职务
				opinion.setType(2);
				opinion.setLevelSortId(Integer.MAX_VALUE);
			}else if(account != null && account.getExternalType() != 4){
				//外单位和组织架构的情况
				V3xOrgDepartment department = orgManager.getDepartmentById(targetMemberPostVO.getDepId());
				if(department != null && department.getIsInternal()){
					//主，副，兼
					opinion.setType(1);
					if(targetMemberPostVO.getType() != null && (OrgConstants.MemberPostType.Main.name().equals(targetMemberPostVO.getType().name()) || OrgConstants.MemberPostType.Second.name().equals(targetMemberPostVO.getType().name()))){
						//主副同一个level 获取主岗的level
						targetLevelId = orgManager.getMemberById(affair.getMemberId()).getOrgLevelId();
						if(targetLevelId != null)
						{
							level = orgManager.getLevelById(targetLevelId);
						}
						if(level != null){
							opinion.setLevelSortId(level.getLevelId());
						}
						else{
							opinion.setLevelSortId(Integer.MAX_VALUE);

						}
					}
					if(targetMemberPostVO.getType() != null && OrgConstants.MemberPostType.Concurrent.name().equals(targetMemberPostVO.getType().name())){
						targetLevelId = targetMemberPostVO.getLevelId();
						if(targetLevelId != null)
						{
							level = orgManager.getLevelById(targetLevelId);
						}
						if(level != null){
							opinion.setLevelSortId(level.getLevelId());
						}
						else{
							opinion.setLevelSortId(Integer.MAX_VALUE);

						}

					}

				}else if(department != null && !department.getIsInternal()){
					//编外人员没有职务
					opinion.setType(3);

					opinion.setLevelSortId(Integer.MAX_VALUE);
				}
				else{
					//异常情况
					opinion.setType(4);
					opinion.setLevelSortId(Integer.MAX_VALUE);
				}

			}else{
				//异常情况
				opinion.setType(4);
				opinion.setLevelSortId(Integer.MAX_VALUE);
			}


		}

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void setOpinionValue(EdocOpinion signOpinion, HttpServletRequest request, CtpAffair affair, EdocSummary summary, Comment comment) throws Exception {
		Map parm = ParamUtil.getJsonDomain("comment_deal");
		if (signOpinion.getAffairId() == 0l) {
			signOpinion.setAffairId(affair.getId());
		}
		String attitude = parm.get("extAtt4") == null ? null : (String) parm.get("extAtt4");
		if(Strings.isBlank(attitude)) {
			attitude = Strings.isBlank(comment.getExtAtt4()) ? null : comment.getExtAtt4();
		}
		if (!Strings.isBlank(attitude)) {
			if ("agree".equals(attitude)) {
				signOpinion.setAttribute(2);
			} else if ("disagree".equals(attitude)) {
				signOpinion.setAttribute(3);
			} else {
				signOpinion.setAttribute(1);
			}
		} else {
			signOpinion.setAttribute(GovdocConstant.EDOC_ATTITUDE_NULL);
		}
		signOpinion.setDeptName(comment.getExtAtt1I18n());
		if(parm.get("content_coll")!= null){
			String content_coll=(String)parm.get("content_coll");
			signOpinion.setContent(content_coll);
        }
		if(parm.get("content") != null){
			String content = (String) parm.get("content");
			signOpinion.setContent("null".equals(content)?"":content);
		}
		//批处理设置意见
		if(Strings.isNotBlank(comment.getContent())&&Strings.isBlank(signOpinion.getContent())) {
			signOpinion.setContent(comment.getContent());
		}
		String[] afterSign = request.getParameterValues("afterSign");
		Set<String> opts = new HashSet<String>();
		if (afterSign != null) {
			for (String option : afterSign) {
				opts.add(option);
			}
		}
		// 允许处理后归档和跟踪被同时选中，但是他们都不能和删除按钮同时选中
		if (opts.size() > 1 && opts.contains("delete")) {
			opts.remove("delete");
		}
		signOpinion.isDeleteImmediate = opts.contains("delete");
		boolean track = opts.contains("track");
		signOpinion.affairIsTrack = track;
		signOpinion.isPipeonhole = opts.contains("pipeonhole");
		signOpinion.setIsHidden(request.getParameterValues("isHidden") != null);
		signOpinion.setId(UUIDLong.longUUID());
		if (null != affair.getActivityId()) {
			signOpinion.setNodeId(affair.getActivityId());
		}
		User user = AppContext.getCurrentUser();
		Boolean pishiFlag = false;
		if (summary != null) {
			EdocSummary edocSummary = new EdocSummary();
			edocSummary.setId(summary.getId());
			edocSummary.setFormId(summary.getFormAppid());
			signOpinion.setEdocId(summary.getId());
			signOpinion.setCreateTime(new Timestamp(System.currentTimeMillis()));
			signOpinion.setOpinionType(EdocOpinion.OpinionType.signOpinion.ordinal());
			if (Strings.isBlank(signOpinion.getPolicy()))
				signOpinion.setPolicy(affair.getNodePolicy());
			if (affair.getMemberId().longValue() != user.getId().longValue()) {
				List<AgentModel> agentModelList = MemberAgentBean.getInstance().getAgentModelList(user.getId());
				Map<String, Object> map = AffairUtil.getExtProperty(affair);
				if (map != null && map.get(AffairExtPropEnums.dailu_pishi_mark.toString()) != null) {
					pishiFlag = true;
				}
				if (agentModelList != null && !agentModelList.isEmpty() && !pishiFlag) {
					signOpinion.setProxyName(user.getName());
				}
			}
			signOpinion.setCreateUserId(affair.getMemberId());
			if (null != signOpinion.getContent()) {
				signOpinion.setContent(signOpinion.getContent().replace("<br/>", "\r\n"));
			}
			try {
				List attachList = comment.getAttachList();
				String relateInfo = comment.getRelateInfo();
				if ((Strings.isNotBlank(relateInfo)) && (!relateInfo.startsWith("["))) {
					relateInfo = "[" + relateInfo + "]";
				}
				if (Strings.isNotBlank(relateInfo)) {
					attachList = (List) JSONUtil.parseJSONString(relateInfo, List.class);
				}
				if ((attachList != null) && (attachList.size() > 0)) {
					// List newAttachList = new ArrayList();
					Long[] fileIds = new Long[attachList.size()];
					for (int i = 0; i < attachList.size(); i++) {
						Map<String, String> att = (Map<String, String>) attachList.get(i);
						if (null == att.get("fileUrl")) {
							String attachmentType = att.get("attachment_type");
							if("2".equals(attachmentType)) { // 关联文档
								govdocContentManager.saveEdocOpinionRelationAttachment(i, summary.getId(), signOpinion.getId(), 2, ApplicationCategoryEnum.edoc,
										att.get("attachment_filename"), att.get("attachment_mimeType"), Long.valueOf(att.get("attachment_fileUrl")));
							}else {
								File file = fileManager.getFile(Long.valueOf(att.get("attachment_fileUrl")), new Date());
								if (null != file) {
									V3XFile v3xfile = fileManager.save(file, ApplicationCategoryEnum.edoc, att.get("attachment_filename"), new Date(), true);
									fileIds[i] = v3xfile.getId();
								}
							}
						} else {
							File file = fileManager.getFile(Long.valueOf(att.get("fileUrl")), new Date());
							if (null != file) {
								V3XFile v3xfile = fileManager.save(file, ApplicationCategoryEnum.edoc, att.get("filename"), new Date(), true);
								fileIds[i] = v3xfile.getId();
							}
						}

					}
					attachmentManager.create(fileIds, ApplicationCategoryEnum.edoc, summary.getId(), signOpinion.getId());
					signOpinion.setHasAtt(true);
				}
			} catch (Exception e) {
				LOGGER.error("", e);
			}

		}
	}

	/*************************** 11111 公文单意见保存 end ***************************/

	/*************************** 22222 公文单落款电子签名 start ***************************/
	@SuppressWarnings("rawtypes")
	@Override
	public void saveEdocPicSign(EdocOpinion edocOpinion) {
		// User user = AppContext.getCurrentUser();
		EdocSummary summary = null;
		try {
			summary = govdocSummaryManager.getSummaryById(edocOpinion.getEdocId());
		} catch (BusinessException e3) {
			LOGGER.error("",e3);
		}
		
		long formId = 0;
		try {
			// formId =
			// govdocSummaryManager.getSummaryById(summary.getId()).getFormId();
			// 新公文使用formappid
			formId = govdocSummaryManager.getSummaryById(summary.getId()).getFormAppid();
		} catch (BusinessException e2) {
			LOGGER.error("",e2);
		}
		Map fieldData = ParamUtil.getJsonDomain("comment_deal");
		String hasSaveHW = fieldData.get("hasSaveHW") == null ? "" : (String) fieldData.get("hasSaveHW");
		String chooseOpinionType = fieldData.get("chooseOpinionType") == null ? "" : (String) fieldData.get("chooseOpinionType");

		if ("1".equals(chooseOpinionType) && "".equals(hasSaveHW)) {
			V3xHtmDocumentSignature hs = govdocSignetManager.getBySummaryIdAffairIdAndType(summary.getId(), edocOpinion.getAffairId(),
					V3xHtmSignatureEnum.HTML_SIGNATURE_DOCUMENT.getKey());
			if (hs != null)
				govdocSignetManager.deleteBySummaryIdAffairIdAndType(summary.getId(), edocOpinion.getAffairId(), V3xHtmSignatureEnum.HTML_SIGNATURE_DOCUMENT.getKey());
		}
		V3xHtmDocumentSignature hd = govdocSignetManager.getBySummaryIdAffairIdAndType(summary.getId(), edocOpinion.getAffairId(),
				V3xHtmSignatureEnum.HTML_SIGNATURE_EDOC_FLOW_INSCRIBE.getKey());

		// 公文处理意见回显到公文单,排序
		// long flowPermAccout =
		// GovdocHelper.getFlowPermAccountId(user.getLoginAccount(),
		// edocOpinion.getEdocSummary(), templateManager);

		// 查找公文单意见元素显示。
		FormOptionExtend govdocFormExtend = govdocFormManager.findOptionExtendByFormId(formId);
		FormOpinionConfig displayConfig = JSONUtil.parseJSONString(FormOpinionConfig.getDefualtConfig(), FormOpinionConfig.class);
		// 公文单显示格式
		if (null != govdocFormExtend) {
			displayConfig = JSONUtil.parseJSONString(govdocFormExtend.getOptionFormatSet(), FormOpinionConfig.class);
		}
		if (OpinionShowNameTypeEnum.SIGN.getValue().equals(displayConfig.getShowNameType())) {// 电子签名显示方式
			String postInfo = edocOpinion.getPostInfo();
			Long accountId = null;
			if(Strings.isNotBlank(postInfo))
			{
				String[] orgInfos = postInfo.split("\\|");
				if(orgInfos.length >=2)
				{
					accountId = Long.parseLong(orgInfos[0]);
				}
			}
			List<V3xSignet> ls = govdocSignetManager.findSignetByMemberId(edocOpinion.getCreateUserId());
			String userName = null;

			try {
				V3xOrgMember affairUser = orgManager.getMemberById(edocOpinion.getCreateUserId());
				userName = affairUser.getName();
				if(accountId == null)
				{
					accountId = affairUser.getOrgAccountId();
				}
			} catch (BusinessException e1) {
				userName = "";
				LOGGER.error("保留签名信息，获取人员名称错误", e1);
			}

			if (Strings.isNotEmpty(ls)) {
				Collections.sort(ls, new Comparator<V3xSignet>() {
					public int compare(V3xSignet o1, V3xSignet o2) {
						return o1.getMarkDate().compareTo(o2.getMarkDate());
					}
				});
				V3xSignet vSign = null;
				Integer signType = new Integer(0);// 0 - 签名， 1 - 印章
				for (V3xSignet sign : ls) {
					if (signType.equals(sign.getMarkType())) {
						if(accountId == null)
						{
							vSign = sign;// 获取第一个签名
							break;
						}
						else
						{
							if(sign.getOrgAccountId().longValue() == accountId)
							{
								vSign = sign;// 获取第一个签名
								break;
							}
						}
					}
				}
				if (vSign != null) {
					byte[] markBodyByte = vSign.getMarkBodyByte();

					boolean isUpdate = true;
					if (hd == null) {
						isUpdate = false;
						hd = new V3xHtmDocumentSignature();
					}

					hd.setIdIfNew();
					hd.setAffairId(edocOpinion.getAffairId());// 设置affairId
					hd.setSummaryId(edocOpinion.getEdocId());// 取得文档编号
					hd.setFieldName("");// 取得签章字段名称, 设置为空
					hd.setFieldValue(GovdocHelper.byte2hex(markBodyByte));// 取得签章数据内容,
																			// 16进制方式保存
					hd.setUserName(userName);// 取得用户名称
					hd.setDateTime(new Timestamp(System.currentTimeMillis()));// 取得签章日期时间
					hd.setHostName("");// 取得客户端IP，审核不做IP记录
					hd.setSignetType(V3xHtmSignatureEnum.HTML_SIGNATURE_EDOC_FLOW_INSCRIBE.getKey());

					try {
						if (isUpdate) {
							govdocSignetManager.update(hd);
						} else {
							govdocSignetManager.save(hd);
						}
					} catch (Exception e) {
						LOGGER.error(e.getMessage(), e);
					}
				} else {// 取消签名的情况进行删除
					if(accountId != null && vSign != null && vSign.getOrgAccountId().longValue() == accountId)
					{
						deleteEdocPicSign(edocOpinion);// 删除签名
					}
				}
			}
		} else if (hd != null) {// 文单签名方式改变，删除已有数据
			deleteEdocPicSign(edocOpinion);// 删除签名
		}
	}

	/**
	 * 物理删除文单审批落款电子签名
	 */
	@Override
	public void deleteEdocPicSign(EdocOpinion edocOpinion) {
		govdocSignetManager.deleteBySummaryIdAffairIdAndType(edocOpinion.getEdocId(), edocOpinion.getAffairId(),
				V3xHtmSignatureEnum.HTML_SIGNATURE_EDOC_FLOW_INSCRIBE.getKey());
	}

	/*************************** 22222 公文单落款电子签名 start ***************************/

	/*************************** 33333 公文单意见删除 start ***************************/
	/**
	 * 删除原有意见
	 * 
	 * @throws BusinessException
	 */
	@Override
	public void deleteOldCommentAndOpinion(Long affairId, EdocSummary summary) throws BusinessException {
		List<CtpCommentAll> allDelOpin = ctpCommentManager.findCommentsByAffairId(affairId);
		if (!allDelOpin.isEmpty()) {
			for (CtpCommentAll comment : allDelOpin) {
				ctpCommentManager.deleteComment(ModuleType.edoc, comment.getId());
				attachmentManager.deleteByReference(summary.getId(), comment.getId());
			}
		}
		// 删除文单意见
		List<EdocOpinion> lis = edocOpinionDao.findEdocOpinionByAffairId(affairId);
		if (lis != null && lis.size() > 0) {
			for (EdocOpinion edocOpinion : lis) {
				edocOpinionDao.delete(edocOpinion.getId());
				attachmentManager.deleteByReference(summary.getId(), edocOpinion.getId());
			}
		}
	}

	@Override
	public void deleteOpinionBySummaryId(Long summaryId) {
		edocOpinionDao.deleteOpinionBySummaryId(summaryId);
	}

	/*************************** 33333 公文单意见删除 end ***************************/

	/*************************** 44444 公文单外意见查看 start ***************************/
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Comment getCommnetFromRequest(OperationType optType, Long affairMemberId, Long moduleId) {
		Comment comment = null;
		User user = AppContext.getCurrentUser();
		Map para = ParamUtil.getJsonDomain("comment_deal");
		comment = (Comment) ParamUtil.mapToBean(para, new Comment(), false);
		this.setCommentPost(comment,para.get("postInfo"));
		if (para.get("content_coll") != null) {
			// 发起人意见
			String content_coll = (String) para.get("content_coll");
			// 如果附言内容是 "附言(500字以内)" 说明是没有设置附言，则需要把附言置空
			if (ResourceUtil.getString("collaboration.newcoll.fywbzyl").equals(content_coll.replace("\n", ""))) {
				content_coll = "";
			}
			comment.setContent(content_coll);
		}
		if ("1".equals((String) para.get("praiseInput"))) {
			comment.setPraiseToSummary(true);
		} else {
			comment.setPraiseToSummary(false);
		}
		// 判断是否代理人
		Long userId = user.getId();
		if (!userId.equals(affairMemberId)) {
			comment.setExtAtt2(user.getName());
		}
		if (comment.getModuleId() == null || comment.getModuleId() == 0 || comment.getModuleId() == -1)
			comment.setModuleId(moduleId);
		if (optType == OperationType.wait) {
//			comment.setExtAtt1("");  -- OA-188930
			comment.setExtAtt3("common.save.and.pause.flow");
		}
		return comment;
	}

	@SuppressWarnings("unchecked")
	@Override
	@ProcessInDataSource(name = DataSourceName.BASE)
	public List<Long> getSenderCommentIdByModuleIdAndCtype(ModuleType moduleType, Long moduleId) throws BusinessException {
		String hql = "select id from CtpCommentAll where moduleType=:moduleType and moduleId=:moduleId and cType=:cType";
		Map<String, Object> mapX = new HashMap<String, Object>();
		mapX.put("moduleType", moduleType.getKey());
		mapX.put("moduleId", moduleId);
		mapX.put("cType", CommentType.sender.getKey());
		return DBAgent.find(hql, mapX);
	}

	@Override
	public List<Comment> getCommentAllByModuleId(ModuleType moduleType, Long moduleId) throws BusinessException {
		return ctpCommentManager.getCommentAllByModuleId(moduleType, moduleId);
	}

	@Override
	public List<Comment> getCommentAllByModuleId(ModuleType moduleType, Long moduleId, boolean isHistoryFlag) throws BusinessException {
		return ctpCommentManager.getCommentAllByModuleId(moduleType, moduleId, isHistoryFlag);
	}

	@Override
	public List<Comment> getCommentList(ModuleType moduleType, Long moduleId) throws BusinessException {
		return ctpCommentManager.getCommentList(moduleType, moduleId);
	}

	@Override
	public List<CtpCommentAll> getDealOpinion(Long affairId) throws BusinessException {
		List<CtpCommentAll> ctpCommentAlls = ctpCommentManager.findCommentsByAffairId(affairId);
		for(CtpCommentAll ctpCommentAll:ctpCommentAlls){
			if(ctpCommentAll.getCtype() != CommentType.comment.getKey()){
				ctpCommentAlls.remove(ctpCommentAll);
			}
		}
		return ctpCommentAlls;
	}

	@Override
	public CtpCommentAll getDrfatComment(Long affairId) throws BusinessException {
		return ctpCommentManager.getDrfatComment(affairId);
	}

	@SuppressWarnings("unchecked")
	@Override
	@ProcessInDataSource(name = DataSourceName.BASE)
	public Comment getDraftOpinion(Long affairId) {
		StringBuilder hql = new StringBuilder();
		Map<String, Object> params = new HashMap<String, Object>();
		hql.append(" from CtpCommentAll where affairId=:affairId and ctype=:ctype ");
		params.put("affairId", affairId);
		params.put("ctype", CommentType.draft.getKey());
		List<CtpCommentAll> list = (List<CtpCommentAll>) DBAgent.find(hql.toString(), params);
		Comment comment = null;
		if (Strings.isNotEmpty(list)) {
			comment = new Comment(list.get(0));
		}
		return comment;
	}

	@Override
	public Comment getNullDealComment(Long affairId, Long summaryId) {
		Comment comment = new Comment();
		comment.setId(UUIDLong.longUUID());
		comment.setAffairId(affairId);
		comment.setModuleId(summaryId);
		comment.setCreateId(AppContext.currentUserId());
		comment.setCreateDate(new Timestamp(System.currentTimeMillis()));
		comment.setClevel(1);
		comment.setCtype(CommentType.comment.getKey());
		comment.setHidden(false);
		comment.setContent("");
		comment.setModuleType(ApplicationCategoryEnum.collaboration.getKey());
		comment.setPath("001");
		comment.setPid(0l);
		// TODO 态度，3.5的时候直接归档是没有态度的
		return comment;
	}

	@Override
	public void fillCommentLog(GovdocSummaryVO summaryVO) throws BusinessException {
		String processId = summaryVO.getSummary().getProcessId();
		if(Strings.isBlank(processId)) {
			return;
		}
		Map<String, List<String>> logDescStrMap = new HashMap<String, List<String>>();
		
		// 需要查询的操作
		List<Integer> actionList = new ArrayList<Integer>();
		actionList.add(ProcessLogAction.insertPeople.getKey());// 加签
		actionList.add(ProcessLogAction.colAssign.getKey());// 当前会签
		actionList.add(ProcessLogAction.deletePeople.getKey());// 减签
		actionList.add(ProcessLogAction.inform.getKey());// 知会
		actionList.add(ProcessLogAction.processColl.getKey());// 修改正文
		actionList.add(ProcessLogAction.addMoreSign.getKey());// 多级会签
		actionList.add(ProcessLogAction.addAttachment.getKey());// 增加附件
		actionList.add(ProcessLogAction.deleteAttachment.getKey());// 删除附件
		actionList.add(ProcessLogAction.updateAttachmentOnline.getKey());// 在线编辑附件
		actionList.add(ProcessLogAction.insertCustomDealWith.getKey());// 续办
		// key:commentId, value:操作日志 列表
		
		List<ProcessLog> processLogs = govdocLogManager.getLogsByProcessIdAndActionId(Long.valueOf(processId), actionList);
		Map<Long, List<ProcessLog>> processLogMap = new HashMap<Long, List<ProcessLog>>();
		// 将所有日志放入map
		for (ProcessLog log : processLogs) {
			List<ProcessLog> logs = processLogMap.get(log.getCommentId());
			if (null != logs) {
				logs.add(log);
			} else {
				logs = new ArrayList<ProcessLog>();
				logs.add(log);
			}
			processLogMap.put(log.getCommentId(), logs);
		}
		for (Long commentId : processLogMap.keySet()) {
			List<ProcessLog> logs = processLogMap.get(commentId);
			Boolean addAttachment = false;
			Boolean deleteAttachment = false;
			Boolean updateAttachment = false;
			List<String> logDescs = new ArrayList<String>();
			// 日志描述(key:操作类型，value:操作日志)
			Map<Integer, String> logDescMap = new HashMap<Integer, String>();
			for (ProcessLog log : logs) {
				if (actionList.contains(log.getActionId())) {
					// 是否有添加附件
					if (Integer.valueOf(ProcessLogAction.addAttachment.getKey()).equals(log.getActionId())) {
						if (Strings.isNotBlank(log.getParam0()) && !addAttachment) {
							addAttachment = true;
						}
						// 是否有删除附件
					} else if (Integer.valueOf(ProcessLogAction.deleteAttachment.getKey()).equals(log.getActionId())) {
						if (Strings.isNotBlank(log.getParam0()) && !deleteAttachment) {
							deleteAttachment = true;
						}
						// 是否修改附件
					} else if (Integer.valueOf(ProcessLogAction.updateAttachmentOnline.getKey()).equals(log.getActionId())) {
						if (Strings.isNotBlank(log.getParam0()) && !updateAttachment) {
							updateAttachment = true;
						}
					} else {
						String logString = logDescMap.get(log.getActionId());
						if (logString != null) {
							StringBuilder desc = new StringBuilder(logString);
							desc.append(",").append(log.getParam0());
							logString = desc.toString();
						} else {
							logString = log.getActionDesc();
						}
						logDescMap.put(log.getActionId(), logString);
					}
				}
			}

			// 每次回复的日志操作按照actionList排序
			for (Integer action : actionList) {
				String logDesc = logDescMap.get(action);
				if (null != logDesc) {
					logDescs.add(logDesc);
				}
			}
			// 判断附件操作
			List<String> attachmentOperation = new ArrayList<String>();
			if (addAttachment) {
				attachmentOperation.add(ResourceUtil.getString("common.upload.label"));
			}
			if (deleteAttachment) {
				attachmentOperation.add(ResourceUtil.getString("common.toolbar.delete.label"));
			}
			if (updateAttachment) {
				attachmentOperation.add(ResourceUtil.getString("common.toolbar.update.label"));
			}
			if (attachmentOperation.size() != 0) {
				logDescs.add(ResourceUtil.getString("processLog.action.user.0", Strings.join(attachmentOperation, ",")));
			}
			logDescStrMap.put(String.valueOf(commentId), logDescs);
		}
		String jsonString = JSONUtil.toJSONString(logDescStrMap);
        summaryVO.setLogDescMap(jsonString);
	}

	/*************************** 44444 公文单外意见查看 end ***************************/

	/*************************** 55555 公文单外意见保存 start ***************************/
	@Override
	public Comment saveCommentFromRequest(OperationType optType, Long affairMemberId, Long moduleId) throws BusinessException {
		if (OperationType.personalTemplate.equals(optType)) {
			Long perTemMId = (Long) AppContext.getThreadContext("_perTemModuleId");
			moduleId = perTemMId;
		}
		Comment comment = getCommnetFromRequest(optType, affairMemberId, moduleId);
		Long moduleId2 = comment.getModuleId();
		if (moduleId2 != null) {
			List<CommentType> types = new ArrayList<CommentType>();
			types.add(CommentType.sender);
			ctpCommentManager.deleteCommentAllByModuleIdAndCtypes(ModuleType.edoc, moduleId2, types );
		}
		if ((optType == OperationType.save || optType == OperationType.pTemplateText
				|| optType == OperationType.personalTemplate || optType == OperationType.template)

				&& Strings.isBlank(comment.getContent())) {
			// 保存待发，如果内容为空就不保存了。
		} else {
			comment.setId(UUIDLong.longUUID());

			if (comment.getModuleId() != null && comment.getModuleType() != null) {
				ctpCommentManager.insertComment(comment);
			}
		}
		return comment;
	}

	@Override
	public void saveComment(GovdocNewVO info, CtpAffair affair) throws BusinessException {
		// 保存评论回复
		Comment comment = this.getCommnetFromRequest(OperationType.send, affair.getMemberId(), info.getSummary().getId());
		if(comment.getDepartmentId() == null)
		{
			V3xOrgAccount account = null;
			V3xOrgDepartment department = null;
			V3xOrgPost post = null;
			try {
				account = orgManager.getAccountById(affair.getMatchAccountId());
				department = orgManager.getDepartmentById(affair.getMatchDepartmentId());
				post = orgManager.getPostById(affair.getMatchPostId());
			} catch (BusinessException e) {
				LOGGER.error("获取要保持的落款失败",e);
			}

			if(account != null && department != null)
			{
				comment.setAccountId(account.getId());
				comment.setAccountName(account.getShortName());
				comment.setDepartmentId(department.getId());
				comment.setDepartmentName(department.getName());

			}
			if(post != null){
				comment.setPostId(post.getId());
				comment.setPostName(post.getName());
			}

		}
		// 如果附言内容是 "附言(500字以内)" 说明是没有设置附言
		if (Strings.isNotBlank(comment.getContent()) && !ResourceUtil.getString("collaboration.newcoll.fywbzyl").equals(comment.getContent().replace("\n", ""))) {
			if (comment.getId() != null && comment.getId() != -1) {
				this.deleteCommentAllByModuleIdAndCtype(ModuleType.edoc, info.getSummary().getId());
			}
			comment.setId(UUIDLong.longUUID());
			info.setComment(comment);
			comment.setModuleId(info.getSummary().getId());
			comment.setCreateId(info.getCurrentUser().getId());
			comment.setCreateDate(info.getCurrentDate());
			ctpCommentManager.insertComment(comment);
		}
		if (info.isModifyDealSuggestion()) {
			Comment nibanComment = new Comment();
			try {
				nibanComment = (Comment) BeanUtils.cloneBean(comment);
			} catch (Exception e) {
				LOGGER.error("", e);
			}
			nibanComment.setId(UUIDLong.longUUID());
			nibanComment.setExtAtt2(null);
			nibanComment.setCtype(CommentType.govdocniban.getKey());
			nibanComment.setRelateInfo(null);
			nibanComment.setAttachList(null);
			nibanComment.setPid(0L);
			nibanComment.setContent(info.getSummary().getDealSuggestion());
			nibanComment.setModuleId(info.getSummary().getId());
			nibanComment.setCreateId(info.getCurrentUser().getId());
			nibanComment.setCreateDate(info.getCurrentDate());
			nibanComment.setAffairId(affair.getId());
			nibanComment.setGroupId(affair.getGroupId());
            if(affair.isCannotViewCommentByMainFlow()) {
                nibanComment.setViewGroupId(affair.getGroupId());
            }
			ctpCommentManager.insertComment(nibanComment);
		}
	}

	@Override
	public void updateComment(Comment comment) throws BusinessException {
		ctpCommentManager.updateComment(comment);
	}

	@Override
	public Comment saveOrUpdateComment(Comment c) throws BusinessException {
		Comment comment = c;
		c.setPushMessage(false);
		if (c.getId() == null) {
			comment = this.insertComment(c);
		} else {
			this.updateComment(c);
		}
		return comment;
	}

	@Override
	public Comment saveComment4Repeal(Comment comment, String repealCommentTOHTML, User user, EdocSummary summary, CtpAffair currentAffair) throws BusinessException {
		//批处理进来没有页面表单参数
		if(ParamUtil.getJsonDomain("comment_deal").isEmpty()) {
            comment.setExtAtt1(CommentExtAtt1Enum.disagree.getI18nLabel());
            comment.setExtAtt4(CommentExtAtt1Enum.disagree.name());
		}else {
			ParamUtil.getJsonDomainToBean("comment_deal", comment);
		}
		if (user == null || user.getId() == null) {
			return comment;
		}
		if(Strings.isBlank(comment.getPath())) {
			
		}
		Long userId = user.getId();
		comment.setCreateDate(new Timestamp(System.currentTimeMillis()));
		// TODO 将终止流程的当前Affair放入ThreadLocal，便于工作流中发送消息时获取代理信息。
		comment.setModuleId(summary.getId());
		comment.setExtAtt3("common.toolbar.repeal.label");
		// 查询当前事项的代理人
		Long curAgentIDLong = MemberAgentBean.getInstance().getAgentMemberId(ApplicationCategoryEnum.collaboration.ordinal(), currentAffair.getMemberId());
		// 判断是否是单位管理员
		if (!userId.equals(currentAffair.getMemberId()) && user.isAdmin()) {
			comment.setCreateId(userId);
		} else {
			comment.setCreateId(currentAffair.getMemberId());
		}
		// 判断当前用户是否该事项的代理人
		if (userId.equals(curAgentIDLong) && currentAffair.getMemberId().longValue() != userId.longValue()) {
			comment.setExtAtt2(user.getName());
		}
		if (Strings.isBlank(comment.getContent())) {
			comment.setContent(repealCommentTOHTML);
		}
		comment.setModuleType(ModuleType.edoc.getKey());
		comment.setPid(0L);
		
		comment.setGroupId(currentAffair.getGroupId());
		if(currentAffair.isCannotViewCommentByMainFlow()) {
		    comment.setViewGroupId(currentAffair.getGroupId());
		}
		
		comment = saveOrUpdateComment(comment);
		return comment;
	}

	@Override
	public Comment insertComment(Comment comment, String openFrom) throws BusinessException {
		Long affairId = comment.getAffairId();
		User user = AppContext.getCurrentUser();
		CtpAffair affair = affairManager.get(affairId);
		if (affair != null) {
			if ("supervise".equals(openFrom)) {
				comment.setCreateId(AppContext.currentUserId());
			} else {
				List<Long> ownerIds = MemberAgentBean.getInstance().getAgentToMemberId(ApplicationCategoryEnum.edoc.key(), user.getId());
				boolean isProxy = false;
				if (Strings.isNotEmpty(ownerIds) && ownerIds.contains(affair.getMemberId())) {
					isProxy = true;
				}
				if (!affair.getMemberId().equals(user.getId()) && isProxy) {
					comment.setExtAtt2(AppContext.getCurrentUser().getName());
					comment.setCreateId(affair.getMemberId());
				} else {
					comment.setCreateId(AppContext.currentUserId());
					comment.setExtAtt2(null);
				}
			}
			if ("".equals(comment.getTitle())) {
				comment.setTitle(affair.getSubject());
			}
			//添加所在部门
			Long departmentId = affair.getMatchDepartmentId() == null ?
					affair.getOrgDepartmentId() : affair.getMatchDepartmentId();
			V3xOrgDepartment department = orgManager.getDepartmentById(departmentId);
	        comment.setDepartmentName(department.getName());
	        comment.setDepartmentId(department.getId());
	        //人员后面是否显示部门开关
	        String displayDepartment = systemConfig.get("displayDepartment_enable");
	        if (Strings.isNotBlank(displayDepartment) && "enable".equals(displayDepartment)) {
	        	comment.setDepartmentFullName(department.getWholeName().replace(",", "/"));
	        }
		} else {
			comment.setCreateId(AppContext.currentUserId());
		}
		
		if (Strings.isNotBlank(comment.getPushMessageToMembers()) && !"[]".equals(comment.getPushMessageToMembers())) {//进行过一次附言后pushMessageToMembers的值设置为了[],此处过滤掉
			comment.setPushMessage(true);
		}
		String showToId = comment.getShowToId();
		if(showToId != null && showToId.indexOf(",") >-1) {
			String[] arr = showToId.split(",");
			showToId = arr[0].replace("Member|", "");
		}
		Comment c = ctpCommentManager.insertComment(comment);
		//c.setCreateName(Functions.showMemberName(comment.getCreateId()));
		c.setShowToId(showToId);
		// 发送评论/回复的消息
		sendMsg(c);

		GovdocDealVO dealVo = new GovdocDealVO();
		dealVo.setComment(c);
		GovdocEventDispatcher.fireEdocAddCommentEvent(this, dealVo);
		
		EdocSummary summary = govdocSummaryManager.getSummaryById(Long.valueOf(comment.getModuleId()));
		if(affair != null){
			govdocLogManager.insertProcessLog(AppContext.getCurrentUser(), Long.parseLong(summary.getProcessId()), affair.getActivityId() == null ? -1L : affair.getActivityId(),
					ProcessLogAction.processEdoc, String.valueOf(ProcessLogAction.ProcessEdocAction.replyYiJian.getKey()));
		}
		String accountName = orgManager.getMemberById(comment.getCreateId()).getName();
		govdocLogManager.insertAppLog(user, GovdocAppLogAction.EDOC_REPLY_OPINION.key(), accountName, summary.getSubject());
		// 全文检索库 入库
		try {
			if (AppContext.hasPlugin("index")) {
				if (affairId != null) {
					if (Integer.valueOf(ModuleType.edoc.getKey()).equals(comment.getModuleType())) {
						CtpAffair ctpAffair = affairManager.get(affairId);
						if (ctpAffair != null) {// 添加回复更新全文检索库
						    indexApi.update(ctpAffair.getObjectId(), ApplicationCategoryEnum.edoc.getKey());
						}
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("", e);
		}
		return c;
	}

	@Override
	public Comment insertComment(Comment comment, CtpAffair affair) throws BusinessException {
		// OA-193492 如果当前切换的是兼职单位，那么createname应该显示兼职单位
		User user = AppContext.getCurrentUser();
		if (user != null) {
			if (user.isAdmin()) {
				//管理员处理,名字回填为当前处理的用户名
				V3xOrgMember v3xOrgMember = orgManager.getMemberById(comment.getCreateId());
				if (v3xOrgMember != null)
				{
					comment.setCreateName(v3xOrgMember.getName());
				}
			} else if (!Strings.equals(user.getLoginAccount(), user.getAccountId())) { // 不是同一个单位的
				V3xOrgAccount account = OrgHelper.getAccount(user.getLoginAccount());
				if (account == null) {
					comment.setCreateName(user.getName());
				}
				comment.setCreateName(user.getName() + "(" + account.getShortName() + ")");
			}
			else {
				V3xOrgAccount account = OrgHelper.getAccount(user.getLoginAccount());
				if (account == null) {
					comment.setCreateName(user.getName());
				}
				comment.setCreateName(user.getName() + "(" + account.getShortName() + ")");
			}
		}
		return ctpCommentManager.insertComment(comment,affair);
	}

	@Override
	public Comment insertComment(Comment comment) throws BusinessException {
		return ctpCommentManager.insertComment(comment);
	}

	@Override
	public Comment saveDealNibanComment(GovdocDealVO dealVo,Comment comment, CtpAffair affair) throws BusinessException {
		// 处理拟办意见
		if (comment != null) {
			try {
				if (dealVo.isModifyDealSuggestion()) {
					Comment nibanComment = new Comment();
					try {
						nibanComment = (Comment) BeanUtils.cloneBean(comment);
					} catch (Exception e) {
						LOGGER.error("", e);
					}
					// add by rz 2017-09-11 [添加代录人判断] start
					String pishiFlag;
					try {
						pishiFlag = govdocPishiManager.checkLeaderPishi(AppContext.getCurrentUser().getId(), affair.getMemberId());
						if ("pishi".equals(pishiFlag)) {
							nibanComment.setContent(nibanComment.getContent() + ResourceUtil.getString("govdoc.By") + AppContext.currentUserName() + ResourceUtil.getString("govdoc.generation.record"));
						}
					} catch (Exception e) {
						LOGGER.error("", e);
					}
					// add by rz 2017-09-11 [添加代录人判断] end
					nibanComment.setId(UUIDLong.longUUID());
					nibanComment.setExtAtt2(null);
					nibanComment.setCtype(CommentType.govdocniban.getKey());
					nibanComment.setRelateInfo(null);
					nibanComment.setAttachList(null);
					nibanComment.setPid(0L);
					nibanComment.setContent(dealVo.getSummary().getDealSuggestion());
					nibanComment.setModuleId(dealVo.getSummary().getId());
					nibanComment.setCreateId(dealVo.getCurrentUser().getId());
					nibanComment.setCreateDate(dealVo.getCurrentDate());
					nibanComment.setAffairId(affair.getId());
					nibanComment.setGroupId(affair.getGroupId());
		            if(affair.isCannotViewCommentByMainFlow()) {
		                nibanComment.setViewGroupId(affair.getGroupId());
		            }
					ctpCommentManager.insertComment(nibanComment);
				}
			} catch (Exception e) {
				LOGGER.error("创建拟办意见失败", e);
				throw new BusinessException(e);
			}
//			if ("true".equals(fieldData.get("resetComment"))) {// 避免重复
//				comment = null;
//			}
		}
		return comment;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Comment saveNibanComment(Comment comment) {
		Map mapa = ParamUtil.getJsonDomain("colMainData");
		String hasnibanComment = ParamUtil.getString(mapa, "hasnibanComment");
		if (Strings.isNotBlank(hasnibanComment) && !"0".equals(hasnibanComment)) {
			Comment nibanComment = new Comment();
			try {
				nibanComment = (Comment) BeanUtils.cloneBean(comment);
			} catch (Exception e) {
				LOGGER.error("", e);
				//e.printStackTrace();
			}
			nibanComment.setId(UUIDLong.longUUID());
			nibanComment.setExtAtt2(null);
			nibanComment.setCtype(CommentType.govdocniban.getKey());
			nibanComment.setRelateInfo(null);
			nibanComment.setAttachList(null);
			nibanComment.setPid(0L);
			if ("2".equals(hasnibanComment)) {// 保留全部意见
				nibanComment.setContent(ParamUtil.getString(mapa, "content_deal_comment"));
				return nibanComment;
			} else if ("1".equals(hasnibanComment)) {// 保存空意见
				nibanComment.setContent("");
				return nibanComment;
			}

		}
		return null;
	}

	@Override
	public Comment insertNibanComment(Comment comment) throws BusinessException {
		return ctpCommentManager.insertComment(comment);
	}

	private void sendMsg(Comment comment) throws BusinessException {

		boolean ispush = comment.isPushMessage() != null && comment.isPushMessage();

		// 回复意见的时候不管是否勾选了发送消息，需要给跟踪人发送消息
		if (comment.getCtype() == CommentType.reply.getKey() && !ispush) {
			ispush = true;
			comment.setPushMessageToMembers("");
		}
		// 评论回复消息推送

		if (ispush) {
			ContentConfig cc = null;
			if (comment.getModuleType() == ModuleType.edoc.getKey()) {
				cc = ContentConfig.getConfig(ModuleType.edoc);
			} else {
				cc = ContentConfig.getConfig(ModuleType.getEnumByKey(comment.getModuleType()));
			}

			ContentInterface ci = cc.getContentInterface();
			// 包含了发起人附言和回复的消息
			if (ci != null) {
				ci.doCommentPushMessage(comment);
			}
		}
	}

	/*************************** 55555 公文单外意见保存 end ***************************/

	/*************************** 66666 公文单外意见删除 start ***************************/
	@Override
	@ProcessInDataSource(name = DataSourceName.BASE)
	public void deleteNibanCommentAllByModuleIdAndCtype(ModuleType moduleType, Long moduleId) throws BusinessException {
		String hql = "delete from CtpCommentAll where moduleType=:moduleType and moduleId=:moduleId and cType=:cType";
		Map<String, Object> mapX = new HashMap<String, Object>();
		mapX.put("moduleType", moduleType.getKey());
		mapX.put("moduleId", moduleId);
		mapX.put("cType", CommentType.govdocniban.getKey());
		DBAgent.bulkUpdate(hql, mapX);
	}

	@Override
	public void deleteCommentAllByModuleIdAndCtypes(ModuleType moduleType, Long moduleId, List<CommentType> types) throws BusinessException {
		ctpCommentManager.deleteCommentAllByModuleIdAndCtypes(moduleType, moduleId, types);
	}

	@Override
	public void deleteCommentAllByModuleId(ModuleType moduleType, Long moduleId) throws BusinessException {
		ctpCommentManager.deleteCommentAllByModuleId(moduleType, moduleId);
	}

	@Override
	public void deleteCommentAllByModuleIdAndCtype(ModuleType moduleType, Long moduleId) throws BusinessException {
		ctpCommentManager.deleteCommentAllByModuleIdAndCtype(moduleType, moduleId);
	}

	@Override
	public void deleteComment(ModuleType moduleType, Long commentId) throws BusinessException {
		ctpCommentManager.deleteComment(moduleType, commentId);
	}

	/*************************** 66666 公文单外意见删除 end ***************************/
	@SuppressWarnings({ "rawtypes", "deprecation", "unchecked" })
	@AjaxAccess
	@Override
	public Map<String, Object> findsSummaryComments(Map<String, String> params) throws BusinessException {

		// 是否查询转储数据
		Boolean isHistory = "true".equals(params.get("isHistory"));
		// 协同ID
		Long moduleId = Long.valueOf(params.get("moduleId"));
		Long affairId = ParamUtil.getLong(params, "affairId");
        
        CtpAffair affair = affairManager.get(affairId);
        if(affair == null) {
            throw new IllegalArgumentException("no affairId in the request post data.");
        }
        
		// 页数
		Integer page = Integer.valueOf(params.get("page"));
		// 锚点定位的意见Id
		Long anchorCommentId = ParamUtil.getLong(params, "anchorCommentId", null);
		// 每页的条数
		Integer pageSize = Integer.valueOf(params.get("pageSize"));
		// 查当前意见还是转发意见[ 0|全部，1|转发，2|当前]
		String queryType = params.get("queryType");
		String paramForwardCountKeys = params.get("forwardCountKeys");
		String forwardMember = params.get("forwardMember"); // 当前协同是否是转发的协同
		String summaryCommentsCounts = params.get("replyCounts");

		//boolean isForwardColl = false;
		int summaryCommentsCount = 0;
		if (Strings.isNotBlank(forwardMember)) {
			//isForwardColl = true;
		}
		if (Strings.isNotBlank(summaryCommentsCounts)) {
			summaryCommentsCount = Integer.valueOf(summaryCommentsCounts);
		}

		boolean isQueryAll = "0".equals(queryType);
		// 是否需要查询转发意见
		boolean isQueryFoward = false;
		// 是否需要查询当前意见
		boolean isQueryCurrent = false;

		// 协同ID
		FlipInfo fpi = new FlipInfo();
		fpi.setSize(pageSize);
		fpi.setNeedTotal(false);
		fpi.setPage(page);

		// 当前意见列表
		List<Comment> commentList = null;
		// 当前发起人附言
		List<Comment> commentSenderList = null;
		// 转发的意见 - [转发的次数 ：[意见类型（评论\回复\发起人附言等） : 对应类型的意见列表]]
		Map<Integer, Map<Integer, List<Comment>>> resultMap = new HashMap<Integer, Map<Integer, List<Comment>>>();
		// 转发的点赞数[转发的次数：点赞数]
		Map<Integer, Integer> forwardPraise = new HashMap<Integer, Integer>();
		// 主协同点赞数
		int praiseToSumNum = 0;
		// 意见总数
		Map<Integer, Integer> allCommentCount = new HashMap<Integer, Integer>();

		// 总的意见数是否大于一页（包括转发的意见+本身的）
		boolean allMorePageSize = false;
		// 当前意见数是否大于一页
		boolean currentMorePageSize = false;
		// 转发的意见数是否大于一页
		boolean forwardMorePageSize = false;
		//
		List<Long> affairIds = new ArrayList<Long>();
		// 是否有转发的意见
		List<Integer> forwardCountKeys = new ArrayList<Integer>();
		if (Strings.isNotBlank(paramForwardCountKeys)) {
			String[] arr = paramForwardCountKeys.split("[,]");
			for (String s : arr) {
				forwardCountKeys.add(Integer.valueOf(s));
			}
		}
		Integer allCnt = 0;
		int forwardCnt = 0;
		int currentCnt = 0;
		if (isQueryAll) {
			//if (isForwardColl) { 处理意见数 不正确  对比V6.1时没有这个逻辑的，并且只有协同才会有转发start  by chenx
			Map<Integer, Map<Integer, Integer>> cntMap = ctpCommentManager.countComments(ModuleType.edoc, moduleId,new HashMap<String, Object>(), isHistory);
			Collection<Integer> a = cntMap.keySet();

			if (Strings.isNotEmpty(a)) {
				for (Integer forwardCountKey : a) {
					Map<Integer, Integer> m = cntMap.get(forwardCountKey);
					Collection<Integer> ctypes = m.keySet();
					for (Integer ctype : ctypes) {
						int count = m.get(ctype) == null ? 0 : m.get(ctype);
						allCnt += count;
						// 是否有转发意见
						if (Integer.valueOf(0).equals(forwardCountKey)) {
							currentCnt += count;
						} else {
							forwardCnt += count;
							forwardCountKeys.add(forwardCountKey);
						}

						if (ctype.equals(CommentType.comment.getKey()) || ctype.equals(CommentType.govdocniban.getKey())) {
							allCommentCount.put(forwardCountKey, allCommentCount.get(forwardCountKey) == null ? count : allCommentCount.get(forwardCountKey) + count);
						}
					}
					if (allCommentCount.get(forwardCountKey) == null) {
						allCommentCount.put(forwardCountKey, 0);
					}
				}
				if (currentCnt > pageSize) {
					currentMorePageSize = true;
				}
				if (allCnt > pageSize) {
					allMorePageSize = true;
				}
				if (forwardCnt > pageSize) {
					forwardMorePageSize = true;
				}
			}
			/*} else {
				allCnt = summaryCommentsCount;
				forwardMorePageSize = false;
				allCommentCount.put(0, summaryCommentsCount);
				if (summaryCommentsCount > pageSize) {
					currentCnt = pageSize;
					currentMorePageSize = true;
					allMorePageSize = true;
				} else {
					currentCnt = summaryCommentsCount;
					currentMorePageSize = false;
					allMorePageSize = false;
				}
			}对比V6.1时没有这个逻辑的，并且只有协同才会有转发end  by chenx*/
		}

		// 第一次查询的时候校验一下是否有数据，有数据才查询，只查转发数据的时候，说明是点的页面的下一条，这个时候就代表肯定有数据了。
		if ((isQueryAll && forwardCnt > 0) || "1".equals(queryType)) {
			isQueryFoward = true;
		}
		// 第一次查询的时候校验一下是否有数据，有数据才查询，指定queryType查询当前意见，说明是点的页面的下一条，这个时候就代表肯定有数据了。
		if ((isQueryAll && currentCnt > 0) || "2".equals(queryType)) {
			isQueryCurrent = true;
		}
		boolean currentHasNext = false;
		boolean forwardHasNext = false;
		
		Long groupId = affair.getGroupId();
        Long viewGroupId = CtpCommentAll.MAIN_PROCESS_GROUPID;
        if(affair.isCannotViewMainFlowComment()) {
            viewGroupId = groupId;
        }
		
		if (!allMorePageSize && isQueryAll) {
			// 全部一次性查出来，包括评论\回复，包括当前的和转发的一起
			Map<String,Object> returnMap = ctpCommentManager.getCommentsWithForward(ModuleType.edoc, moduleId, groupId, viewGroupId, isHistory);
			
			resultMap = (Map<Integer, Map<Integer, List<Comment>>>) returnMap.get("commentForwardMap");
			commentList =  (List<Comment>) returnMap.get("commentList");
			commentSenderList =  (List<Comment>) returnMap.get("commentSenderList");
			praiseToSumNum =  (Integer) returnMap.get("praiseToSumNum");
			forwardPraise = (Map<Integer,Integer>) returnMap.get("forwardPraise");
		} else {
			if (isQueryAll) {
				Map<String, Object> queryParams = new HashMap<String, Object>();
				queryParams.put("praiseToSummary", 1);
				queryParams.put("groupId", groupId);
                queryParams.put("viewGroupId", viewGroupId);

				Map<Integer, Map<Integer, Integer>> cntMap = ctpCommentManager.countComments(ModuleType.edoc, moduleId, queryParams, isHistory);
				// 从双层Map的结构中解析得到点赞数
				parsePraiseCntMap(forwardPraise, cntMap);

				praiseToSumNum = forwardPraise.get(0) == null ? 0 : forwardPraise.get(0);
			}

			if (isQueryFoward) {
				List<Comment> comments = new ArrayList<Comment>();
				if (forwardMorePageSize || page > 1 || !"0".equals(queryType)) {
					Map<String, Object> m = findSummaryCommentsByPage(fpi, isHistory, moduleId, groupId, viewGroupId, forwardCountKeys, null);
					forwardHasNext = (Boolean) m.get("Next");
					comments = (List<Comment>) m.get("CommentList");
				} else {
					// 全部一次性查出来，包括评论\回复

					Map<String, Object> queryParams = new HashMap<String, Object>();
					List<Integer> ctypes = new ArrayList<Integer>();
					ctypes.add(CommentType.comment.getKey());
					ctypes.add(CommentType.sender.getKey());
					ctypes.add(CommentType.reply.getKey());
					queryParams.put("ctypes", ctypes);
					queryParams.put("forwardCounts", forwardCountKeys);
					FlipInfo commentsFip = ctpCommentManager.findComments(ModuleType.edoc, moduleId, null, queryParams, isHistory);
					comments = commentsFip.getData();
				}

				bindFatherSonRelation(comments);

				packageComment(resultMap, comments);
			}

			if (isQueryCurrent) {

				List<Comment> currentComments = new ArrayList<Comment>();
				if (currentMorePageSize || page > 1 || !"0".equals(queryType)) {
					// 1、查一页的评论
					List<Integer> forwardCount0 = new ArrayList<Integer>();
					forwardCount0.add(0);
					Map m = findSummaryCommentsByPage(fpi, isHistory, moduleId, groupId, viewGroupId, forwardCount0, anchorCommentId);
					currentHasNext = (Boolean) m.get("Next");
					currentComments = (List<Comment>) m.get("CommentList");
					if (null != anchorCommentId) {
						commentList = currentComments;
					}
				} else {
					// 全部一次性查出来，包括评论\回复

					Map<String, Object> queryParams = new HashMap<String, Object>();
					List<Integer> ctypes = new ArrayList<Integer>();
					ctypes.add(CommentType.comment.getKey());
					ctypes.add(CommentType.sender.getKey());
					ctypes.add(CommentType.reply.getKey());
					queryParams.put("ctypes", ctypes);
					List<Integer> forwardCount0 = new ArrayList<Integer>();
					forwardCount0.add(0);
					queryParams.put("forwardCounts", forwardCount0);
					FlipInfo currentCommentsFpi = ctpCommentManager.findComments(ModuleType.edoc, moduleId, null, queryParams, isHistory);
					currentComments = currentCommentsFpi.getData();
				}

				bindFatherSonRelation(currentComments);

				packageComment(resultMap, currentComments);
			}

			// 组装树
			for (Integer forwardCount : resultMap.keySet()) {
				if (forwardCount == 0) {
					Map<Integer, List<Comment>> comMap = resultMap.remove(0);
					for (Integer ctype : comMap.keySet()) {
						if (ctype == 0) {
							commentList = comMap.get(ctype);
						} else if (ctype == -1) {
							commentSenderList = comMap.get(ctype);
						}
					}
					break;
				}
			}
		}
		boolean isSenderAnchor = false;
		if (anchorCommentId != null && Strings.isNotEmpty(commentSenderList)) {
			for (Comment c : commentSenderList) {
				if (c.getId().equals(anchorCommentId)) {
					isSenderAnchor = true;
					break;
				}
			}
		}

		List needDelete = new ArrayList();

		// 锚点定位
		if (anchorCommentId != null && !isSenderAnchor) {
			boolean hasFind = false;
			if (Strings.isNotEmpty(commentList) && allMorePageSize) {
				for (Iterator<Comment> it = commentList.iterator(); it.hasNext();) {
					Comment c = it.next();
					List<Comment> childrens = c.getChildren();
					boolean isChild = false;
					if (Strings.isNotEmpty(childrens)) {
						for (Comment child : childrens) {
							if (child.getId().equals(anchorCommentId)) {
								isChild = true;
								break;
							}
						}
					}
					if (c.getId().equals(anchorCommentId) || isChild) {
						hasFind = true;
					} else {
						// it.remove();
						needDelete.add(c);
					}
				}
			}

			Integer _count = allCommentCount.get(0);
			if (_count != null && _count > 1) {
				currentHasNext = true; // 相当于给了一个默认值
			}

			// 流程追索穿透的时候 存在锚点参数 但是不会意见定位
			if (hasFind) {
				commentList.removeAll(needDelete);
			}
			if (null == _count) {
				_count = 0;
			}
			if ((!hasFind && _count != null && _count <= pageSize) || !allMorePageSize) {
				currentHasNext = false;
			}

		}

		// summary.REPLY_COUNTS 为空，老数据没有升级的时候，做一次兼容。
		if (summaryCommentsCount == 0 && Strings.isNotEmpty(commentList)) {
			compatibleOldNullData(commentList.size(), moduleId, isHistory, allMorePageSize);
		}
		Map<String, Object> retMap = new HashMap<String, Object>();
		// Convert转为前台需要的数据格式
		ConvertVO(resultMap, commentList, commentSenderList);
		if(Strings.isNotEmpty(commentList)) {
			Map<String, Object> poliNames = new HashMap<String, Object>();
			
			for(Comment c : commentList) {
				if(!affairIds.contains(c.getAffairId())) {
					affairIds.add(c.getAffairId());
				}
			}
			if(Strings.isNotEmpty(affairIds)) {
				EdocSummary summary = govdocSummaryManager.getSummaryById(moduleId);
				List<CtpAffair> affairList = govdocAffairManager.getAffairList(affairIds);
				for(CtpAffair a : affairList) {
					poliNames.put(String.valueOf(a.getId()), govdocWorkflowManager.getPolicyByAffair(a, summary).getName());
				}
				retMap.put("policyNames", poliNames);
			}
		}
		
		if(Strings.isNotEmpty(commentList)) {
			Map map = govdocPishiManager.getLeaderPishiByAffairIds(affairIds);
			Map<Long, GovdocComment> removeMap = new HashMap<Long, GovdocComment>();
			List<GovdocComment> listGovdocComment = new ArrayList<GovdocComment>();
			GovdocComment govdocComment = null;//转化为公文的意见类
			for(Comment comment : commentList) {
				govdocComment = new GovdocComment();
				try {
					BeanUtils.copyProperties(govdocComment, comment);
					if(govdocComment.getCreateId() != null){
						V3xOrgMember commentMember = orgManager.getMemberById(govdocComment.getCreateId());
						if(govdocComment.getAccountId() != null && govdocComment.getDepartmentId() != null && commentMember.getOrgAccountId().longValue() != govdocComment.getAccountId()){
							String tempAccountName = govdocComment.getAccountName();
							if(Strings.isBlank(tempAccountName)) {
								V3xOrgDepartment tempDepart = orgManager.getDepartmentById(govdocComment.getDepartmentId());
								tempAccountName = orgManager.getAccountById(tempDepart.getOrgAccountId()).getShortName();
							}
							if(tempAccountName != null) {
								V3xOrgAccount tempAccount = orgManager.getAccountById(govdocComment.getAccountId());
								boolean isSetAccountName = true;
								if(tempAccount != null &&tempAccount.getExternalType() == 4 && tempAccount.getCreaterId() != null){
									V3xOrgMember memberBiz = orgManager.getMemberById(tempAccount.getCreaterId());
									if(memberBiz.getOrgAccountId().longValue() == commentMember.getOrgAccountId().longValue()){
										isSetAccountName = false;
									}
								}
								if(isSetAccountName){
									govdocComment.setCreateName(commentMember.getName()+"(" + tempAccountName + ")");
								}
								else{
									govdocComment.setCreateName(commentMember.getName());
								}

							}else {
								govdocComment.setCreateName(commentMember.getName());
							}
						}else{
							govdocComment.setCreateName(commentMember.getName());
						}
					}
					if(govdocComment.getContent()==null){
						govdocComment.setContent("");
					}
					//拷贝回复意见
					if(Strings.isNotEmpty(comment.getChildren())) {
						List<Comment> childens = comment.getChildren();
						for (Comment c : childens) {
							ConvertCommentAttr(c);
							govdocComment.addChild(c);
						}
					}
				} catch (Exception e) {
					LOGGER.error("公文意见转换错误" + e);
				}
				EdocLeaderPishiNo edocLeaderPishiNo = (EdocLeaderPishiNo)map.get(comment.getAffairId());
				if(Strings.isBlank(comment.getExtAtt3())|| "common.toolbar.stepBack.label".equals(comment.getExtAtt3())) {
					if(removeMap.get(comment.getAffairId()) != null)
					{
						GovdocComment tempComment  = removeMap.get(comment.getAffairId());
						if(govdocComment.getCreateDate().getTime() > tempComment.getCreateDate().getTime())
						{
							govdocComment.setPishiName(tempComment.getPishiName());
							govdocComment.setPishiNo(tempComment.getPishiNo());
							govdocComment.setPishiYear(tempComment.getPishiYear());
							govdocComment.setProxyDate(tempComment.getProxyDate());
							govdocComment.setProxyDate1(tempComment.getProxyDate1());
							tempComment.setPishiName(null);
							tempComment.setPishiNo(null);
							tempComment.setPishiYear(null);
							tempComment.setProxyDate(null);
							tempComment.setProxyDate1(null);
							removeMap.put(comment.getAffairId(),govdocComment);
						}
						else if(govdocComment.getCtype() == 0)
						{
							govdocComment.setPishiName(tempComment.getPishiName());
							govdocComment.setPishiNo(tempComment.getPishiNo());
							govdocComment.setPishiYear(tempComment.getPishiYear());
							govdocComment.setProxyDate(tempComment.getProxyDate());
							govdocComment.setProxyDate1(tempComment.getProxyDate1());
						}
	
					}
					else if(edocLeaderPishiNo != null){//处理批示编号部分
						govdocComment.setPishiName(edocLeaderPishiNo.getPishiName());
						govdocComment.setPishiNo(edocLeaderPishiNo.getPishiNo());
						govdocComment.setPishiYear(edocLeaderPishiNo.getPishiYear());
						govdocComment.setProxyDate(edocLeaderPishiNo.getProxyDate());
						if(edocLeaderPishiNo.getProxyDate() != null) {
							govdocComment.setProxyDate1(Datetimes.formatDate(edocLeaderPishiNo.getProxyDate()));	
						}
						map.remove(comment.getAffairId());//批示编号通过affairId保证唯一,一个批示编号只能set一个意见
						removeMap.put(comment.getAffairId(),govdocComment);
					}
				}
				listGovdocComment.add(govdocComment);
			}
			/************************** 公文意见根据职位等级排序 start ***************************/
			/**
			 *
			 *  @author sheHaiTao
			 *  @Description 公文意见根据职位等级排序
			 *  @data 2021/11/13 - 17:14
			 */
			//用于存评论的
			List<GovdocComment> mysortList = new ArrayList();
			//以comment为主键，以clevel为值，值来自人员的等级
			HashMap<Long, Integer> mylevel = new HashMap<Long, Integer>();
			for (GovdocComment temp: listGovdocComment) {
				Long createId = temp.getCreateId();
				V3xOrgMember memberById = orgManager.getMemberById(createId);
				Long orgLevelId = memberById.getOrgLevelId();
				V3xOrgLevel levelById = orgManager.getLevelById(orgLevelId);
				//用于到时候恢复评论的clevel
				mylevel.put(temp.getId(), temp.getClevel());
				temp.setClevel(levelById.getLevelId());
				//  LOGGER.info("排序的level----"+levelById.getLevelId());
				mysortList.add(temp);
			}
			mysortList.sort(new Comparator<GovdocComment>() {
				@Override
				public int compare(GovdocComment t1, GovdocComment t2) {
					return t1.getClevel()-t2.getClevel();
				}
			});
			//LOGGER.info("排序中" + mysortList.toString());
			//现在的 sortlist已经排序好了，把clevel恢复
			for (GovdocComment comment : mysortList) {
				comment.setClevel(mylevel.get(comment.getId()));
			}
			retMap.put("commentList", mysortList); // 当前意见
			/************************** 公文意见根据职位等级排序 end ***************************/
		}

		retMap.put("commentSenderList", commentSenderList == null ? new ArrayList<Comment>() : commentSenderList); // 发起人附言
		retMap.put("commentForwardMap", resultMap); // 转发的意见
		retMap.put("praiseToSumNum", praiseToSumNum); // 当前意见的赞数
		retMap.put("allPraiseCountMap", forwardPraise); // 转发意见的赞数
		retMap.put("allCommentCountMap", allCommentCount); // 意见总数的计数
		retMap.put("forwardCountKeys", Strings.join(forwardCountKeys, ","));

		// 校验是否有下一页的数据

		retMap.put("currentHasNext", String.valueOf(currentHasNext));// 当前意见是否有下一页
		retMap.put("forwardHasNext", String.valueOf(forwardHasNext)); // 校验转发区是否有下一页

		// TODO 是不是还有更多数据
		return retMap;
	}

	/**
	 * 获取岗位信息列表
	 * @param memberId 人员id
	 * @return
	 * @throws BusinessException
	 */
	private List<MemberPostEdocVO> getMeberPostVOList(Long memberId) throws BusinessException {
		List<MemberPostEdocVO> memberPostVOList = new ArrayList<MemberPostEdocVO>();
		if(memberId == null)
		{
			return memberPostVOList;
		}
		V3xOrgMember currentMember = orgManager.getMemberById(memberId);
		if(currentMember == null)
		{
			return memberPostVOList;
		}
		List<MemberPost> postList = new ArrayList<MemberPost>();
		MemberPost mainPost = MemberPost.createMainPost(currentMember);
		postList.add(mainPost);
		List<MemberPost> secondPostList = currentMember.getSecond_post();
		if(secondPostList!= null && !secondPostList.isEmpty())
		{
			postList.addAll(secondPostList);
		}

		List<MemberPost> concurrentPostList = currentMember.getConcurrent_post();
		if(concurrentPostList!= null && !concurrentPostList.isEmpty())
		{
			postList.addAll(concurrentPostList);
		}

		for(MemberPost tempPost : postList)
		{

			Long tempAccountId = tempPost.getOrgAccountId()== null ? -1l:tempPost.getOrgAccountId();
			Long tempDeptId = tempPost.getDepId()== null ? -1l:tempPost.getDepId();
			Long tempPostId = tempPost.getPostId()== null ? -1l:tempPost.getPostId();
			V3xOrgAccount account = orgManager.getAccountById(tempAccountId);
			String tempAccountName = ResourceUtil.getString("govdoc.no.unit.name");
			if(account!= null )
			{
				if(Strings.isNotBlank(account.getShortName())){
					tempAccountName = account.getShortName();
				}else {
					tempAccountName = account.getName();
				}
			}

			String tempDeptName = ResourceUtil.getString("govdoc.no.department.name");
			V3xOrgDepartment depart =  orgManager.getDepartmentById(tempDeptId);
			if(depart != null)
			{
				tempDeptName  = depart.getName();
			}

			String tempPostName = "";
			V3xOrgPost post = orgManager.getPostById(tempPostId);
			if(post!= null)
			{
				tempPostName = post.getName();
			}
			MemberPostEdocVO memberPostVO = new MemberPostEdocVO(tempAccountName,tempDeptName,tempPostName,"",tempPost);
			memberPostVOList.add(memberPostVO);
		}
		//获取主副兼所在的部门岗位Id
		Set<Long> departmentSet = new HashSet<Long>();
		Set<Long> postSet = new HashSet<Long>();
		for(MemberPostEdocVO temp:memberPostVOList){
			Long  departIdTemp= temp.getDepId();
			if(departIdTemp != null && departIdTemp != -1){
				departmentSet.add(departIdTemp);
			}
			Long postIdTemp = temp.getPostId();
			if(postIdTemp != null && postIdTemp != -1){
				postSet.add(postIdTemp);
			}
		}

		List<V3xOrgRelationship> relationDepart = businessOrgManagerDirect.getMemberBusinessDepartment(memberId,postSet,departmentSet);
		List<MemberPost> bussMemberPost = new ArrayList<MemberPost>();
		if(Strings.isNotEmpty(relationDepart))
		{
			List<MemberRole> roles = orgManager.getMemberRoles(memberId, null);
			Map<Long,List<MemberRole>> targetRolesMap = new HashMap<Long,List<MemberRole>>();
			List<MemberRole> targetRoles = new ArrayList<MemberRole>();
			//获取部门的角色
			if(Strings.isNotEmpty(roles)){
				for(MemberRole role: roles)
				{
					if(role.getDepartment() != null){
						targetRoles.add(role);
					}
				}
			}

			for(V3xOrgRelationship ship: relationDepart)
			{
				Long departmentIdShip = ship.getSourceId();
				List<MemberRole> tempRoleList = targetRolesMap.get(departmentIdShip);
				if(tempRoleList == null){
					tempRoleList = new ArrayList<MemberRole>();
					targetRolesMap.put(departmentIdShip,tempRoleList);
				}
				if(Strings.isNotEmpty(targetRoles)){
					for(MemberRole roleTemp:targetRoles){

						if(roleTemp.getDepartment().getId().longValue() == departmentIdShip.longValue()){
							tempRoleList.add(roleTemp);
						}
					}
				}

			}

			for(V3xOrgRelationship ship: relationDepart)
			{

				Long departmentIdShip = ship.getSourceId();

				List<MemberRole> tempRoleList = targetRolesMap.get(departmentIdShip);
				if(Strings.isNotEmpty(tempRoleList)){
					for(MemberRole tempRole:tempRoleList){
						MemberPost memberPost = MemberPost.createBusinessPost(memberId, departmentIdShip, tempRole.getRole().getId(), ship.getOrgAccountId(), ship.getSortId());
						bussMemberPost.add(memberPost);

					}
				}
				else{
					MemberPost memberPostNoRole = MemberPost.createBusinessPost(memberId,departmentIdShip,-1l,ship.getOrgAccountId(),ship.getSortId());
					bussMemberPost.add(memberPostNoRole);
				}
			}
		}
		List<MemberPostEdocVO>  bussMemberListVO = new ArrayList<MemberPostEdocVO>();
		if(Strings.isNotEmpty(bussMemberPost))
		{
			for(MemberPost tempMemberPost :bussMemberPost){
				Long tempAccountId = tempMemberPost.getOrgAccountId()== null ? -1l:tempMemberPost.getOrgAccountId();
				Long tempDeptId = tempMemberPost.getDepId()== null ? -1l:tempMemberPost.getDepId();
				Long tempRoleId =  tempMemberPost.getBusinessRoleId() == null ? -1l:tempMemberPost.getBusinessRoleId();
				V3xOrgAccount account = orgManager.getAccountById(tempAccountId);
				String tempAccountName = ResourceUtil.getString("govdoc.no.unit.name");
				if(account!= null )
				{
					if(Strings.isNotBlank(account.getShortName())){
						tempAccountName = account.getShortName();
					}else {
						tempAccountName = account.getName();
					}
				}

				String tempDeptName = ResourceUtil.getString("govdoc.no.department.name");
				V3xOrgDepartment depart =  orgManager.getDepartmentById(tempDeptId);
				if(depart != null)
				{
					tempDeptName  = depart.getName();
				}

				String roleName ="";
				V3xOrgRole role = orgManager.getRoleById(tempRoleId);
				Long roleSortId = Long.MAX_VALUE;
				if(role != null){
					roleName = role.getShowName();
					roleSortId = role.getSortId();
				}

				MemberPostEdocVO memberPostVO = new MemberPostEdocVO(tempAccountName,tempDeptName,"",roleName,tempMemberPost);
				bussMemberListVO.add(memberPostVO);
			}
		}
		if(Strings.isNotEmpty(bussMemberListVO)){
			memberPostVOList.addAll(bussMemberListVO);
		}
		return memberPostVOList;
	}

	private void ConvertVO(Map<Integer, Map<Integer, List<Comment>>> resultMap, List<Comment> commentList, List<Comment> commentSenderList)throws BusinessException {
		//Map<Integer, Map<Integer, List<Comment>>> retVOMap = new HashMap<Integer, Map<Integer, List<Comment>>>();
		Set<Integer> keys = resultMap.keySet();
		if (keys != null) {
			for (Integer forwardCount : keys) {
				Map<Integer, List<Comment>> m = resultMap.get(forwardCount);
				Set<Integer> ctypes = m.keySet();
				for (Integer ctype : ctypes) {
					List<Comment> list = m.get(ctype);
					for (Comment c : list) {
						ConvertCommentAttr(c);
					}
				}
			}
		}
		if (Strings.isNotEmpty(commentList)) {
			for (Comment c : commentList) {
				ConvertCommentAttr(c);
			}
		}
		if (Strings.isNotEmpty(commentSenderList)) {
			for (Comment c : commentSenderList) {
				ConvertCommentAttr(c);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void repairAttachment(Comment comment) {
		String atts = comment.getRelateInfo();
		if (Strings.isNotBlank(atts) && atts.indexOf(":") != -1) {
			try {
				List list = JSONUtil.parseJSONString(atts, List.class);
				List<Attachment> l = ParamUtil.mapsToBeans(list, Attachment.class, false);
				l = attachmentManager.setOfficeTransformEnable(l);
				comment.setRelateInfo(JSONUtil.toJSONString(l));
			} catch (Exception e) {
				LOGGER.error("", e);
			}
		}
	}

	private void ConvertCommentAttr(Comment c) throws BusinessException {
		repairAttachment(c);
		if (null != c.getCreateId()) {
			c.setAvatarImageUrl(OrgHelper.getAvatarImageUrl(c.getCreateId()));
		}
		//人员后面是否显示部门开关
        String displayDepartment = systemConfig.get("displayDepartment_enable");
        if (Strings.isNotBlank(displayDepartment) && "enable".equals(displayDepartment)) {
        	if (null == c.getDepartmentId()) {
        		V3xOrgMember member = orgManager.getMemberById(c.getCreateId());
        		if(member != null) {
					c.setDepartmentId(member.getOrgDepartmentId());
				}
        	} 
        	V3xOrgDepartment department = orgManager.getDepartmentById(c.getDepartmentId());
        	if(department != null) {
				c.setDepartmentName(department.getName());
				c.setDepartmentFullName(department.getWholeName().replace(",", "/"));
			}
        }
	}

	@SuppressWarnings({ "unused", "deprecation", "unchecked" })
	private void compatibleOldNullData(int cnt, Long moduleId, boolean isHistory, boolean allMorePageSize) {
		int _cnt = cnt;
		if (allMorePageSize) {
			try {
				Map<String,Object> resultMap = ctpCommentManager.getCommentsWithForward(ModuleType.edoc, moduleId, isHistory);
				List<Comment> commentList = (List<Comment>) resultMap.get("commentList");
				_cnt = commentList.size();
			} catch (BusinessException e) {
				LOGGER.error("", e);
			}
		}
		// colDao.updateColSummaryReplyCounts(moduleId, _cnt);
	}

	private void packageComment(Map<Integer, Map<Integer, List<Comment>>> resultMap, List<Comment> comments) {
		if (Strings.isNotEmpty(comments)) {
			for (Comment comment : comments) {
				Integer forwardCount = comment.getForwardCount();

				// 默认转发次数为0
				if (forwardCount == null)
					forwardCount = 0;
				Map<Integer, List<Comment>> curMap = resultMap.get(forwardCount);
				if (curMap == null) {
					curMap = new HashMap<Integer, List<Comment>>();
					resultMap.put(forwardCount, curMap);
				}
				Integer ctype = comment.getCtype();
				if (ctype == null) // 回复和评论在一个List中，为创建树结构做准备
					ctype = 0;
				List<Comment> comList = curMap.get(ctype);
				if (comList == null) {
					comList = new ArrayList<Comment>();
					curMap.put(ctype, comList);
				}
				//comment.setCreateName(Functions.showMemberName(comment.getCreateId()));
				comList.add(comment);
			}
		}
	}

	private Map<String, Object> findSummaryCommentsByPage(FlipInfo fpi, Boolean isHistory, Long moduleId, Long groupId, Long viewGroupId, 
	        List<Integer> forwardCountKeys, Long anchorCommentId) throws BusinessException {

		Map<String, Object> retMap = new HashMap<String, Object>();

		// 1、查一页的评论

		Map<String, Object> queryParams = new HashMap<String, Object>();
		List<Integer> ctypes = new ArrayList<Integer>();
		Long replyCommentId = anchorCommentId; // 锚点意见对应的评论的意见Id,如果是
		if (null != anchorCommentId) {
			Comment parComment = ctpCommentManager.getComment(anchorCommentId);
			if (parComment != null && Integer.valueOf(CommentType.sender.getKey()).equals(parComment.getCtype())) {
				replyCommentId = null;
			} else {
				if (null != parComment && null != parComment.getPid() && 0l != parComment.getPid().longValue()) {
					replyCommentId = parComment.getPid();
				}
				ctypes.add(CommentType.reply.getKey());
			}

		}
		ctypes.add(CommentType.comment.getKey());
		ctypes.add(CommentType.sender.getKey());
		queryParams.put("ctypes", ctypes);
		queryParams.put("forwardCounts", forwardCountKeys);
		queryParams.put("qMoreOne", true);
		queryParams.put("anchorReplyCommentId", replyCommentId);
		queryParams.put("groupId", groupId);
        queryParams.put("viewGroupId", viewGroupId);
		
		fpi = ctpCommentManager.findComments(ModuleType.edoc, moduleId, fpi, queryParams, isHistory);
		List<Comment> comments = fpi.getData();
		boolean next = comments.size() == fpi.getSize() + 1;
		if (next && null == anchorCommentId) {
			comments.remove(comments.size() - 1);
		}

		// 2、查一页的评论对应的回复意见，锚点就不需要再查询一次了，前面SQL一次性查下出来了
		if (anchorCommentId == null) {
			List<Long> parentIds = new ArrayList<Long>();
			if (Strings.isNotEmpty(comments)) {
				for (Comment c : comments) {
					parentIds.add(c.getId());
				}
			}
			if (Strings.isNotEmpty(parentIds)) {
				Map<Long, List<Comment>> replyMap = ctpCommentManager.findCommentReplay(parentIds);

				List<Comment> replys = getReplys(replyMap);
				comments.addAll(replys);
			}

		}

		retMap.put("CommentList", comments);
		retMap.put("Next", next);
		return retMap;
	}

	/**
	 * @param replyMap
	 * @return
	 */
	private List<Comment> getReplys(Map<Long, List<Comment>> replyMap) {
		Collection<List<Comment>> values = replyMap.values();
		List<Comment> ret = new ArrayList<Comment>();
		if (Strings.isNotEmpty(values)) {
			for (List<Comment> c : values) {
				ret.addAll(c);
			}
		}
		return ret;
	}

	private void parsePraiseCntMap(Map<Integer, Integer> forwardPraise, Map<Integer, Map<Integer, Integer>> cntMap) {
		Collection<Integer> a = cntMap.keySet();
		if (Strings.isNotEmpty(a)) {
			for (Integer forwardCountKey : a) {
				Map<Integer, Integer> m = cntMap.get(forwardCountKey);
				Collection<Integer> ctypes = m.keySet();
				for (Integer ctype : ctypes) {
					int count = m.get(ctype) == null ? 0 : m.get(ctype);
					if (ctype.equals(CommentType.comment.getKey())) {
						forwardPraise.put(forwardCountKey, count);
					}

				}
			}
		}
	}

	private void bindFatherSonRelation(List<Comment> comList) {
		if (comList != null) {
			Map<Long, Comment> allCommentMap = new HashMap<Long, Comment>();
			for (Comment com : comList) {
				allCommentMap.put(com.getId(), com);
			}

			for (Comment c : comList) {
				Long pid = c.getPid();
				if (pid != null && pid != 0) {
					Comment parent = allCommentMap.get(pid);
					if (parent != null) {
						parent.addChild(c);
					}
				}
			}
		}
	}

	@Override
	@ProcessInDataSource(name = DataSourceName.BASE)
	public void editComment(GovdocCommentVO commentVO) throws BusinessException {
	 	String outInfo = "";
	 	String opinionContent = commentVO.getModifyContent();
	 	//add by rz 2017-09-06 [添加批示编号修改保存]  start
	 	String pishiYears = commentVO.getPishiYears();
	 	String pishinos = commentVO.getPishinos();
	 	String proxydates = commentVO.getProxydates();
	 	//add by rz 2017-09-06 [添加批示编号修改保存]  end
	 	//opinionContent = specialString(opinionContent);
	 	long opinionId = commentVO.getEditOpinionId();
	 	User user = AppContext.getCurrentUser();
	 	boolean hasEdit = govdocOpenManager.hasEditAuth(user.getId(), user.getAccountId());
	 	CommentManager commentManager = (CommentManager)AppContext.getBean("ctpCommentManager");
	 	// 修改协同的意见
	 	Comment comment = commentManager.getComment(opinionId);
	 	EdocLeaderPishiNo edocLeaderPishiNo= null;
		boolean  isAllowEditInForm= govdocOpenManager.isAllowEditInForm();
		boolean allowEditMyOpinions="true".equals(SystemEnvironment.getPluginDefinition("govdoc").getPluginProperty("govdoc.allowEditInForm"))
					&& isAllowEditInForm;
		commentVO.getSwitchVo().setNewGovdocView(govdocOpenManager.getGovdocViewValue(user.getId(), user.getLoginAccount()));
	 	//代领导录入关系
	 	String lederRelations = getLederRelations(user.getId());
	 	if(hasEdit||(allowEditMyOpinions && null!=comment && (user.getId().longValue()==comment.getCreateId()||lederRelations.contains(comment.getCreateId().toString())))){//具有编辑公文意见的权限
	 		//保存附件
			AttachmentEditHelper editHelper = commentVO.getAttachmentEditHelper();
	 		try {
	 			String attListJSON = "";
				CtpAffair affair = affairManager.get(commentVO.getAffairId());
				EdocSummary summary = govdocSummaryManager.getSummaryById(comment.getModuleId());
				//协同意见附件修改
				if (editHelper.hasEditAtt()) {//是否修改附件
					 commentVO.setHasEditAtt(true);
					 List<Attachment> commentAtts =attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.edoc,comment.getModuleId(), opinionId, commentVO.getRequest());
					 //生成附件流程日志
	                 try{
	                  this.initAttLog(comment.getModuleId(), opinionId, commentAtts, summary, affair);
	                 }catch(Exception e){
	                	 LOGGER.error("编辑意见生成流程日志时出错:", e);
	                 }
	                 if (Strings.isNotBlank(commentVO.getIsEditAttachment())) {
	                    	attachmentManager.deleteByReference(comment.getModuleId(), opinionId);
	                 }
					 for(Attachment att:commentAtts){
	                    	att.setCreatedate(new Date());
	                    	att.setCategory(ApplicationCategoryEnum.collaboration.getKey());;
	                 }
	                 attachmentManager.create(commentAtts);
	                 if(CommentType.govdocniban.getKey() == comment.getCtype()){//如果修改的是拟办意见,同步修改公文
	                	 List<Attachment> edocAtts = new ArrayList<Attachment>();
	                	 for (Attachment attachment : commentAtts) {
	                		 Attachment att = (Attachment)attachment.clone();
	                		 att.setCategory(ApplicationCategoryEnum.edoc.getKey());
	                		 att.setReference(comment.getModuleId());
	                		 att.setSubReference(comment.getModuleId());
	                		 edocAtts.add(att);
	                	 }
	                	 attachmentManager.deleteByReference(comment.getModuleId(), comment.getModuleId());
	                	 attachmentManager.create(edocAtts);
	                 }	                
	                 attListJSON = this.attachmentManager.getAttListJSON4JS(comment.getModuleId(), opinionId);
				}else if("1".equals(commentVO.getClearAttr())){
					attachmentManager.deleteByReference(comment.getModuleId(),opinionId);
					attListJSON = this.attachmentManager.getAttListJSON4JS(comment.getModuleId(), opinionId);
				}
				if(!"common.save.and.pause.flow".equals(comment.getExtAtt3()) 
						//&& !comment.getIsNiban()
						){
					List<EdocOpinion> edocList = edocOpinionDao.findEdocOpinionByAffairId(comment.getAffairId());
					for (EdocOpinion opinion : edocList) {
						opinion.setContent(opinionContent);
						if (editHelper.hasEditAtt()) {//是否修改附件
						opinion.setHasAtt(editHelper.attSize() > 0);
						EdocSummary edocSummary = govdocSummaryManager.getSummaryById(opinion.getEdocId());
	                   //公文意见附件创建
	                   if (Strings.isNotBlank(commentVO.getIsEditAttachment())) {
	                   	attachmentManager.deleteByReference(edocSummary.getId(), opinion.getId());
	                   }
	                   //获取公文附件
	                   List<Attachment> atts =attachmentManager.getAttachmentsFromRequest(ApplicationCategoryEnum.edoc, edocSummary.getId(), opinion.getId(), commentVO.getRequest());
	                   List<Attachment> lastAtt = new ArrayList<Attachment>();
	                   //修改创建日期及关联ID
	                   for(Attachment att:atts){	                	
		                   	att.setCreatedate(new Date());
		                   	att.setSubReference(opinion.getId());
		                   	lastAtt.add(att);	                	
	                   }
	                   attachmentManager.create(lastAtt);
	                   List<Attachment> attachments = attachmentManager.getByReference(edocSummary.getId(), opinion.getId());
	                   opinion.setOpinionAttachments(attachments);
	                   govdocPubManager.saveUpdateAttInfo(editHelper.attSize(), edocSummary.getId(),new ArrayList<ProcessLog>());
						}
						edocOpinionDao.update(opinion);
						//由于开关文单内修改意见，如果编辑意见后，需要刷新，这里需要刷新整个页面
						commentVO.getSwitchVo().setNewGovdocView("refresh");
					}
				}
				//只有意见修改开关开启并且有权限才能修改意见
				if(!hasEdit){
					opinionContent=comment.getContent();
				}
				//add by rz 2017-09-06[保存批示编号修改信息] start
				if(pishiYears!=null&&!"".equals(pishiYears.trim())&&
						pishinos!=null&&!"".equals(pishinos.trim())){
				 	List<Long> affairIds = new ArrayList<Long>();
				 	affairIds.add(comment.getAffairId());
				 	Map<Long, EdocLeaderPishiNo> map = govdocPishiManager.getLeaderPishiByAffairIds(affairIds);
				 	if(!map.isEmpty() && map.get(comment.getAffairId()) != null){
						edocLeaderPishiNo = map.get(comment.getAffairId());
						edocLeaderPishiNo.setAffairId(comment.getAffairId());
						edocLeaderPishiNo.setPishiNo(Integer.valueOf(pishinos));
						edocLeaderPishiNo.setPishiYear(pishiYears);
						edocLeaderPishiNo.setSummaryId(comment.getModuleId());
						if(proxydates!=null && !"".equals(proxydates)){
							edocLeaderPishiNo.setProxyDate(Datetimes.parse(proxydates, Datetimes.dateStyle));	
						}
						govdocPishiManager.saveOrEditPishiNo(edocLeaderPishiNo);
				 	}
				}
				//add by rz 2017-09-06[保存批示编号修改信息] end
				//保存协同的修改信息
				if(opinionContent != null){
					comment.setContent(opinionContent);
				}
				if(attListJSON !=null && !"".equals(attListJSON)){
					comment.setRelateInfo(attListJSON);
				}
		        CtpCommentAll commentAll = DBAgent.get(CtpCommentAll.class, comment.getId());
		        comment.mergeCommentAll(commentAll);
		        //允许修改成空意见
		        if(opinionContent != null){
		        	commentAll.setContent(opinionContent);
				}
		        //公文移动端存在保存了富文本的情况,修改时需要同步修改
		        if(commentAll.getModuleType() == ApplicationCategoryEnum.edoc.getKey()
		        		&& Strings.isNotBlank(commentAll.getRichContent()) ){
		        	commentAll.setRichContent(commentAll.getContent());
		        }
		        DBAgent.update(commentAll);
				if(hasEdit){
					//保存流程日志			
					String operContent = AppContext.getCurrentUser().getName()+ResourceUtil.getString("govdoc.modify")+Functions.showMemberName(comment.getCreateId())+ResourceUtil.getString("govdoc.edit.comments")+opinionContent;
					govdocLogManager.insertProcessLog(AppContext.getCurrentUser(), Long.parseLong(summary.getProcessId()),
				                affair.getActivityId()==null?-1:affair.getActivityId(), ProcessLogAction.processEdoc,String.valueOf(ProcessLogAction.ProcessEdocAction.modifyYiJian.getKey()),operContent);
					//保存应用日志
					String accountName = orgManager.getMemberById(comment.getCreateId()).getName();
					govdocLogManager.insertAppLog(user, GovdocAppLogAction.EDOC_MODIFY_OPINION.key(), accountName);
				}
				//修改拟办意见
//				if(Comment.CommentType.govdocniban.getKey() == comment.getCtype()){
//	                Long formAppId = summary.getFormAppid();
//	        		FormBean formBean = govdocFormManager.getForm(formAppId);
//	        		String nibanyijian = "";
//	        		for (FormFieldBean fieldBean : formBean.getAllFieldBeans()) {
//	        			if ("nibanyijian".equals(fieldBean.getMappingField())) {
//	        			nibanyijian = fieldBean.getName();
//	        				break;
//	        			}
//	        		}
//	        		FormDataMasterBean formDataMasterBean = formApi4Cap3.findDataById(summary.getFormRecordid(),summary.getFormAppid(),null);
//	        		formDataMasterBean.addFieldValue(nibanyijian, opinionContent);
//	        		formApi4Cap3.saveOrUpdateFormData(formDataMasterBean, formAppId);
//				}
				outInfo = ResourceUtil.getString("govdoc.save.success");
				commentVO.setOutInfo(outInfo);
			} catch (Exception e) {
				LOGGER.error("执行意见修改异常！", e);
			}
	 	}else{
	 		outInfo = ResourceUtil.getString("govdoc.Nopermission.operation");
	 		commentVO.setOutInfo(outInfo);
	 		commentVO.setReturnType(false);
	 	}
	 }
	@Override
	public String delComment(String commentId) throws BusinessException{
		String reStr = "1";
		try {
			if (StringUtils.isNotBlank(commentId)) {
				long comId = Long.valueOf(commentId);
				// 删除意见
			 	CommentManager commentManager = (CommentManager)AppContext.getBean("ctpCommentManager");
			 	Comment comment = commentManager.getComment(comId);
				CtpAffair affair = affairManager.get(comment.getAffairId());
				EdocSummary summary = govdocSummaryManager.getSummaryById(comment.getModuleId());

//				if(comment.getAffairId()!=null){
//	     			CtpAffair aff = affairManager.get(comment.getAffairId());
//			    	String configItem = collaborationApi.getPolicyByAffair(aff).getName();
//			    	comment.setPolicyName(configItem);
//				}
				String operContent = AppContext.getCurrentUser().getName()+ResourceUtil.getString("govdoc.delete.comments")+Functions.showMemberName(comment.getCreateId()) +" "+comment.getCreateDateStr() +" "+affair.getNodePolicy()+" "+comment.getContent();
				if(CommentType.govdocniban.getKey() == comment.getCtype())
					attachmentManager.deleteByReference(comment.getModuleId(), comment.getModuleId());
				if(!"common.save.and.pause.flow".equals(comment.getExtAtt3()) 
						&& CommentType.govdocniban.getKey() != comment.getCtype()){
					List<EdocOpinion> edocList = edocOpinionDao.findEdocOpinionByAffairId(comment.getAffairId());
					if(edocList != null &edocList.size()>0){
						for (EdocOpinion edocOpinion : edocList) {
							Date beforeTwoSecond = Datetimes.addSecond(edocOpinion.getCreateTime(), -3);
							Date afterTwoSecond = Datetimes.addSecond(edocOpinion.getCreateTime(), 3);
							if(comment.getCreateDate().equals(edocOpinion.getCreateTime()) || (
								Datetimes.between(comment.getCreateDate(), beforeTwoSecond, afterTwoSecond, false))) {
								
								edocOpinionDao.delete(edocOpinion);
							}
						}
					}
				}
				//add by rz 2017-12-01[删除意见后同步删除批示编号表的信息] start
				govdocPishiManager.emptyPishiNoByAffairId(comment.getAffairId(),"");
				//add by rz 2017-12-01[删除意见后同步删除批示编号表的信息] end
				commentManager.deleteComment(ModuleType.edoc,comId);
			    commentManager.deleteCommentAllByModuleIdAndParentId(ModuleType.getEnumByKey(comment.getModuleType()), comment.getModuleId(), comId);
			    govdocLogManager.insertProcessLog(AppContext.getCurrentUser(), Long.parseLong(summary.getProcessId()),
						affair.getActivityId() == null?-1L:affair.getActivityId(),ProcessLogAction.processEdoc,String.valueOf(ProcessLogAction.ProcessEdocAction.deleteYiJian.getKey()),operContent);
				//保存应用日志
				String accountName = orgManager.getMemberById(comment.getCreateId()).getName();
				govdocLogManager.insertAppLog(AppContext.getCurrentUser(), GovdocAppLogAction.EDOC_DELETE_OPINION.key(), accountName);
				LOGGER.info(AppContext.getCurrentUser().getName()+"于"+DateUtil.format(new Date(), DateUtil.YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_PATTERN)+"删除ID为"
						+ commentId + ",summaryID："+comment.getModuleId()+"，affairId:"+comment.getAffairId()+"协同意见");
			}
		} catch (Exception e) {
			reStr = "0";
			LOGGER.error("执行意见修改异常！", e);
		}

		return reStr;
	}
	private String getLederRelations(Long userId){
		StringBuilder lederRelation=new StringBuilder();
    	List<EdocUserLeaderRelation> lerderRelations;
		try {
			lerderRelations = govdocPishiManager.getEdocUserLeaderRelation(userId);
    	for(EdocUserLeaderRelation relation:lerderRelations){
    		lederRelation.append(relation.getLeaderId()).append(",");
    	}
		} catch (Exception e) {
			LOGGER.error("->",e);
		}
    	return lederRelation.toString();
	}
	/**
	 * 生成编辑意见附件操作日志 
	 * @param moduleId
	 * @param opinionId
	 * @param commentAtts
	 * @param summary
	 * @param affair
	 */
	private void initAttLog(long moduleId,long opinionId,List<Attachment> commentAtts,EdocSummary summary,CtpAffair affair) throws Exception{
		 List<Attachment>  hasAttList = attachmentManager.getByReference(moduleId, opinionId);
		 Map<String,String> diffMap = new HashMap<String,String>();
		 for(Attachment att:commentAtts){
			 boolean isExist =false;
			 hLoop:for(Attachment hAtt:hasAttList){
				 if(att.getFilename().equals(hAtt.getFilename())){
					 isExist =true;
					 break hLoop;
				 }
			 }	
			 if(!isExist){
				 diffMap.put(att.getFilename(), "add");
			 } 
		 }
		 for(Attachment hasAtt:hasAttList){
			 boolean isExist =false;
			 cLoop:for(Attachment natt:commentAtts){
				 if(hasAtt.getFilename().equals(natt.getFilename())){
					 isExist =true;
					 break cLoop;
				 }
			 }	
			 if(!isExist){
				 diffMap.put(hasAtt.getFilename(), "del");
			 } 
		 }
		 for (Map.Entry<String,String> entry : diffMap.entrySet()) {
		    //没有找到是新增，找到是删除
			 String operContent = "";
			 operContent = entry.getKey();
			 govdocLogManager.insertProcessLog(AppContext.getCurrentUser(), Long.parseLong(summary.getProcessId()),
			                affair.getActivityId()==null?-1:affair.getActivityId(), ProcessLogAction.updateAttachment,operContent); 
		 }
		
	}
	
	@Override
	public List<EdocOpinion> findEdocOpinionByAffairId(Long affairId) throws BusinessException {
		return edocOpinionDao.findEdocOpinionByAffairId(affairId); 
	}
	
	@Override
	public List<Map<String, String>> getAttitudeList(String permissionId) {
		List<Map<String, String>> attitudeList = new ArrayList<Map<String, String>>();
		if (Strings.isBlank(permissionId)) {
			return attitudeList;
		}
		PermissionVO permission = null;
        try{
            permission = permissionManager.getPermission(Long.valueOf(permissionId));
        } catch (Exception e) {
        	LOGGER.error("获取节点权限失败", e);
		}
        if(permission != null){
        	StringBuffer nodeattitude = new StringBuffer();
        	
        	String attitude = permission.getAttitude();
        	DetailAttitude detailAttitude = permission.getDatailAttitude();
        	
        	if (Strings.isNotBlank(attitude)) {
        		String[] attitudeArr = attitude.split(",");
        		for (String att : attitudeArr) {
        			Map<String, String> attitudeMap = new HashMap<String,String>();
        			if (Strings.isNotBlank(nodeattitude.toString())) {
        				nodeattitude.append(",");
        			}
        			if ("haveRead".equals(att)) {
        				attitudeMap.put("value", detailAttitude.getHaveRead());
        				attitudeMap.put("showValue", ResourceUtil.getString(detailAttitude.getHaveRead()));
        				nodeattitude.append(detailAttitude.getHaveRead());
        			} else if ("agree".equals(att)) {
        				attitudeMap.put("value", detailAttitude.getAgree());
        				attitudeMap.put("showValue", ResourceUtil.getString(detailAttitude.getAgree()));
        				nodeattitude.append(detailAttitude.getAgree());
        			} else if ("disagree".equals(att)) {
        				attitudeMap.put("value", detailAttitude.getDisagree());
        				attitudeMap.put("showValue", ResourceUtil.getString(detailAttitude.getDisagree()));
        				nodeattitude.append(detailAttitude.getDisagree());
        			} 
        			attitudeMap.put("code", att);
        			
        			attitudeList.add(attitudeMap);
        		}
        	}
        }
    	return attitudeList;
	}

	/**
	 * 设置岗位信息
	 * @param comment
	 * @param param
	 * @return
	 */
	@Override
	public boolean setCommentPost(Comment comment, Object param) {
		if (param != null) {
			// 发起人意见
			String postInfo = (String)param;

			String[] orgInfos = null;
			if(Strings.isNotBlank(postInfo))
			{
				orgInfos = postInfo.split("\\|");
			}
			if(orgInfos != null && orgInfos.length >2)
			{
				V3xOrgAccount account = null;
				V3xOrgDepartment department = null;
				V3xOrgPost post = null;
				try {
					account = orgManager.getAccountById(Long.parseLong(orgInfos[0]));
					department = orgManager.getDepartmentById(Long.parseLong(orgInfos[1]));
					post = orgManager.getPostById(Long.parseLong(orgInfos[2]));
				} catch (BusinessException e) {

				}

				if(account != null && department != null)
				{
					comment.setAccountId(account.getId());
					comment.setAccountName(account.getShortName());
					comment.setDepartmentId(department.getId());
					comment.setDepartmentName(department.getName());
				}
				if(post != null){
					comment.setPostId(post.getId());
					comment.setPostName(post.getName());
				}
				return true;
			}

		}
		return false;
	}
	/*************************** 99999 Spring注入，请将业务写在上面 start ******************************/
	public void setCtpCommentManager(CommentManager ctpCommentManager) {
		this.ctpCommentManager = ctpCommentManager;
	}
	public void setEdocOpinionDao(EdocOpinionDao edocOpinionDao) {
		this.edocOpinionDao = edocOpinionDao;
	}
	public void setGovdocSummaryManager(GovdocSummaryManager govdocSummaryManager) {
		this.govdocSummaryManager = govdocSummaryManager;
	}
	public void setGovdocWorkflowManager(GovdocWorkflowManager govdocWorkflowManager) {
		this.govdocWorkflowManager = govdocWorkflowManager;
	}
	public void setGovdocContentManager(GovdocContentManager govdocContentManager) {
		this.govdocContentManager = govdocContentManager;
	}
	public void setGovdocLogManager(GovdocLogManager govdocLogManager) {
		this.govdocLogManager = govdocLogManager;
	}
	public void setAffairManager(AffairManager affairManager) {
		this.affairManager = affairManager;
	}
	public void setAttachmentManager(AttachmentManager attachmentManager) {
		this.attachmentManager = attachmentManager;
	}
	public void setFileManager(FileManager fileManager) {
		this.fileManager = fileManager;
	}
	public void setIndexApi(IndexApi indexApi) {
        this.indexApi = indexApi;
    }
	public void setOrgManager(OrgManager orgManager) {
		this.orgManager = orgManager;
	}
	public void setGovdocFormManager(GovdocFormManager govdocFormManager) {
		this.govdocFormManager = govdocFormManager;
	}
	public void setGovdocSignetManager(GovdocSignetManager govdocSignetManager) {
		this.govdocSignetManager = govdocSignetManager;
	}
	public void setGovdocPishiManager(GovdocPishiManager govdocPishiManager) {
		this.govdocPishiManager = govdocPishiManager;
	}
	public void setFormApi4Cap3(FormApi4Cap3 formApi4Cap3) {
        this.formApi4Cap3 = formApi4Cap3;
    }
	public void setGovdocOpenManager(GovdocOpenManager govdocOpenManager) {
		this.govdocOpenManager = govdocOpenManager;
	}
	public void setGovdocPubManager(GovdocPubManager govdocPubManager) {
		this.govdocPubManager = govdocPubManager;
	}	
	public void setGovdocAffairManager(GovdocAffairManager govdocAffairManager) {
		this.govdocAffairManager = govdocAffairManager;
	}
	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	/*************************** 99999 Spring注入，请将业务写在上面 end ******************************/

	public SystemConfig getSystemConfig() {
		return systemConfig;
	}

	public void setSystemConfig(SystemConfig systemConfig) {
		this.systemConfig = systemConfig;
	}

	public void setBusinessOrgManagerDirect(BusinessOrgManagerDirect businessOrgManagerDirect) {
		this.businessOrgManagerDirect = businessOrgManagerDirect;
	}


}
