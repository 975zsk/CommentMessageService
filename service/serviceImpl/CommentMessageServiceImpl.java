package cn.edu.bjtu.weibo.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.edu.bjtu.weibo.dao.CommentDAO;
import cn.edu.bjtu.weibo.dao.TopicDAO;
import cn.edu.bjtu.weibo.dao.UserDAO;
import cn.edu.bjtu.weibo.dao.WeiboDAO;
import cn.edu.bjtu.weibo.model.Comment;
import cn.edu.bjtu.weibo.model.Topic;
import cn.edu.bjtu.weibo.model.User;
import cn.edu.bjtu.weibo.service.CommentMessageService;
import cn.edu.bjtu.weibo.service.MessageToMeService;

@Service("commentMessageService")
public class CommentMessageServiceImpl implements CommentMessageService{
	@Autowired
	private UserDAO userDAO;
	@Autowired
	private CommentDAO commentDAO;
	@Autowired
	private WeiboDAO weiboDAO;	
	@Autowired
	private TopicDAO topicDAO;
	@Autowired
	private MessageToMeService messageToMeService;
	
	// userId �������ߵ�ID       comment������� �����ߵ�ID
	@Override
	public boolean CommentToWeibo(String userId, String weiboId, Comment comment) {
		// TODO Auto-generated method stub
		String content = comment.getContent();
		    
		/*
		 * 
		 * ��һ�� ����# # ��@
		 */
		List valid_topic_index  = new  ArrayList();   //�Ϸ���#
		List topic_index = new ArrayList();  //ȫ����#
		 
	     //�Ƚ���# #,ʶ�����е�#����¼λ��
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 //ɾȥ���Ϸ���#
		 
		 List topic = new ArrayList();   //һ���Ϸ��Ļ���
		 List left_char =new ArrayList();   //��#��index
		 List right_char =new ArrayList();   //��#��index
		
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //����#���ţ���ɾȥ��� ���ұ߱���µ�left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //һ�����ⱻ�ҳ�����
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //����@ɨ��
				   
				   
				   left_char.add(left);
				   right_char.add(right);
				   
				   String topic_str = content.substring(left+1, right);
				   topic.add(topic_str);	
				   
				   left=-1;
			 }
			 
		 }
		
	    System.out.println(left_char.toString());
	    System.out.println(right_char.toString());
	    System.out.println(valid_topic_index.toString());
	    System.out.println(topic.toString());
		
	    
	    //ɨ�� @  �������Ϸ�#  # ���Զ�����
	    int j=0;
	    int skip_index = (int) valid_topic_index.get(j);
	    
	    
	    
	    List at = new ArrayList();   //���@������
	    List at_start_index = new ArrayList();  //@�Ŀ�ʼ
	    List at_end_index = new ArrayList();    //@�Ľ���
	    
	
	    List at_index = new ArrayList();        //ȫ���Ϸ���@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //�ַ������һ����@  �Զ�����
	    	
	    	if(index==skip_index){    //����ط��ǻ���,������������
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //��һ����#
	    		if(j<valid_topic_index.size()){
	    			skip_index=(int)valid_topic_index.get(j);
	    		}	    		
	    		
	    	}else{
	    	    char want = content.charAt(index);
	    	    char want_right = content.charAt(index+1);
	    	
	    	    if(want=='@' && (want_right!='#' && want_right!='!' && want_right!=' ' && want_right!='$' &&
	    			want_right!='%' && want_right!='^' && want_right!='&' && want_right!='*' && want_right!='(' &&
	    			want_right!=')' && want_right!='=' && want_right!='+' && want_right!='{' && want_right!='}' &&
	    			want_right!='@'
	    			)){                                      //����� @+����  @@ @# ������
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want=='!' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //�������� ��Ҫ����
	    		at_end = index-1;
	    		String at_str = content.substring(at_start+1, at_end+1);
	    		at.add(at_str);
	    		at_start_index.add(at_start);
	    		at_end_index.add(at_end);
	    		
	    		k++;
	    		if(k<at_index.size()){
	    		    index = (int) at_index.get(k);
	    		}
	    		at_start=index;
	    		at_end=-1;
	    			
	    	}
	    	//�Ϸ����ַ� ������
	    }
	    
	    //���ǵ� @666 ���۽������ �������ַ�����β�ָ�
	    at_start=(int) at_index.get(k);
	    at_end = content.length()-1;
	    String at_str = content.substring(at_start+1);
	    
	    at.add(at_str);
		at_start_index.add(at_start);
		at_end_index.add(at_end);
	   
	    System.out.println(at.toString());
	    System.out.println(at_start_index.toString());
	    System.out.println(at_end_index.toString());
	    
	    
	    /*
	     * 
	     * �ڶ�����ƴ���µ�content�� �����ۼ������ݿ�
	     * 
	     */
	    
	    
	 
	    //
	    //ƴ���µ�content 
	    String new_content ="";
	    
	    int a=0;  //@
	    int b=0;  //#
	    for(int index=0;index<content.length();index++){
	    	
	    	char temp = content.charAt(index);
	    	String str ="";
	    	if(index==(int)at_start_index.get(a)){  //@
	    		 str = "<a href = \"at��ת����\"><font color=\"blue\">@" +at.get(a)+"</font></a>";	    		
	    		 index =(int) at_end_index.get(a);
	    		 if((a+1)<at.size()){	    		   
	    		    a++;
	    		}
	    		 
	    	}
	    	
	    	else if(index==(int)left_char.get(b)){   //#
	    		 str = "<a href = \"������ת����\"><font color=\"blue\">#" +topic.get(b)+"#</font></a>";		    		 
	    		 index=(int)right_char.get(b);
	    		 if((b+1)<topic.size()){
	    		    b++;
	    		}
	    		 	    			    	 		   	    	
	    	}
	    	
	    	else{
	    		 str = String.valueOf(temp);
	    	}
	    	
	    	new_content=new_content+str;
	    }
	    
	     System.out.println(content);
	     System.out.println(new_content);
	    
	  
	     comment.setCommentNumber(0);
	     comment.setCommentOrWeiboId(weiboId);
	     comment.setLike(0);
	     
	     Date date = new Date();
	     DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String time=format.format(date);
	     comment.setDate(time);
	     comment.setAtUserIdList(at);
	     comment.setTopicIdList(topic);
	     comment.setContent(new_content);
	     
	    String commentId = commentDAO.insertNewComment(comment);  //���������۲������ݿ�,�Ҵ˿���û���������۵�ID ��
	     
	    
	    weiboDAO.insertCommentList(weiboId, commentId); //���� ��΢���������б�
	   
	    userDAO.insertCommentOnWeibo(commentId, weiboId); // �����۹���΢��
	     
	     
	    /*
	     * 
	     * 
	     * ����������������Ļ���� @ ���д���
	     */
	    
	    //�Ի���Ĵ���
		  List<String>  topic_list = new ArrayList<String>();  //���е�topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //���е�topicID
			   
		    
		 //�õ����л��������
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  if(topic_name_list.contains(topic.get(i))){
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertComment(topic_list.get(index), commentId);  //�û�������� 
				  
				  System.out.println("��������⣺"+topic.get(i));
				  
			  }else{    //����һ������
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				 String topicId= topicDAO.insertNewTopic(new_topic);
				 topicDAO.insertComment(topicId, commentId);
				  
				 System.out.println("�½��˻��⣺"+topic.get(i));
			  }
		  }
		    
		  //��@�Ĵ���
		  List<String>  user_list = new ArrayList<String>();  //���е�userID
		  List<String>  user_name_list = new ArrayList<String>();  //���е�userID��name
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //����Map
		  }

		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.contains(at.get(i))){    //�����û�
				  
				  int index=user_name_list.indexOf(at.get(i));
				  messageToMeService.atMeInfromComment(user_list.get(index), commentId);   //�õ���@���û�ID  
				  
				  System.out.println("������û���"+at.get(i));
				  
			  }else{
				  
				 //�û������� ���ùܣ�
				  System.out.println("û����û���"+at.get(i));
			  }
		  }
		  
		  //�ñ����۵���֪��
		  messageToMeService.commentMyCommentInform(userId, commentId);
		  return true;
		
	}

	@Override
	public boolean CommentToComment(String userId, String CommentId, Comment comment) {
		// TODO Auto-generated method stub
        String content = comment.getContent();
		
		/*
		 * 
		 * ��һ�� ����# # ��@
		 */
		List valid_topic_index  = new  ArrayList();   //�Ϸ���#
		List topic_index = new ArrayList();  //ȫ����#
		 
	     //�Ƚ���# #,ʶ�����е�#����¼λ��
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 //ɾȥ���Ϸ���#
		 
		 List topic = new ArrayList();   //һ���Ϸ��Ļ���
		 List left_char =new ArrayList();   //��#��index
		 List right_char =new ArrayList();   //��#��index
		
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //����#���ţ���ɾȥ��� ���ұ߱���µ�left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //һ�����ⱻ�ҳ�����
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //����@ɨ��
				   
				   
				   left_char.add(left);
				   right_char.add(right);
				   
				   String topic_str = content.substring(left+1, right);
				   topic.add(topic_str);	
				   
				   left=-1;
			 }
			 
		 }
		
	    System.out.println(left_char.toString());
	    System.out.println(right_char.toString());
	    System.out.println(valid_topic_index.toString());
	    System.out.println(topic.toString());
		
	    
	    //ɨ�� @  �������Ϸ�#  # ���Զ�����
	    int j=0;
	    int skip_index = (int) valid_topic_index.get(j);
	    
	    
	    
	    List at = new ArrayList();   //���@������
	    List at_start_index = new ArrayList();  //@�Ŀ�ʼ
	    List at_end_index = new ArrayList();    //@�Ľ���
	    
	
	    List at_index = new ArrayList();        //ȫ���Ϸ���@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //�ַ������һ����@  �Զ�����
	    	
	    	if(index==skip_index){    //����ط��ǻ���,������������
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //��һ����#
	    		if(j<valid_topic_index.size()){
	    			skip_index=(int)valid_topic_index.get(j);
	    		}	    		
	    		
	    	}else{
	    	    char want = content.charAt(index);
	    	    char want_right = content.charAt(index+1);
	    	
	    	    if(want=='@' && (want_right!='#' && want_right!='!' && want_right!=' ' && want_right!='$' &&
	    			want_right!='%' && want_right!='^' && want_right!='&' && want_right!='*' && want_right!='(' &&
	    			want_right!=')' && want_right!='=' && want_right!='+' && want_right!='{' && want_right!='}' &&
	    			want_right!='@'
	    			)){                                      //����� @+����  @@ @# ������
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want=='!' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //�������� ��Ҫ����
	    		at_end = index-1;
	    		String at_str = content.substring(at_start+1, at_end+1);
	    		at.add(at_str);
	    		at_start_index.add(at_start);
	    		at_end_index.add(at_end);
	    		
	    		k++;
	    		if(k<at_index.size()){
	    		    index = (int) at_index.get(k);
	    		}
	    		at_start=index;
	    		at_end=-1;
	    			
	    	}
	    	//�Ϸ����ַ� ������
	    }
	    
	    //���ǵ� @666 ���۽������ �������ַ�����β�ָ�
	    at_start=(int) at_index.get(k);
	    at_end = content.length()-1;
	    String at_str = content.substring(at_start+1);
	    
	    at.add(at_str);
		at_start_index.add(at_start);
		at_end_index.add(at_end);
	   
	    System.out.println(at.toString());
	    System.out.println(at_start_index.toString());
	    System.out.println(at_end_index.toString());
	    
	    
	    /*
	     * 
	     * �ڶ�����ƴ���µ�content�� �����ۼ������ݿ�
	     * 
	     */
	    
	    
	 
	    //
	    //ƴ���µ�content 
	    String new_content ="";
	    
	    int a=0;  //@
	    int b=0;  //#
	    for(int index=0;index<content.length();index++){
	    	
	    	char temp = content.charAt(index);
	    	String str ="";
	    	if(index==(int)at_start_index.get(a)){  //@
	    		 str = "<a href = \"at��ת����\"><font color=\"blue\">@" +at.get(a)+"</font></a>";	    		
	    		 index =(int) at_end_index.get(a);
	    		 if((a+1)<at.size()){	    		   
	    		    a++;
	    		}
	    		 
	    	}
	    	
	    	else if(index==(int)left_char.get(b)){   //#
	    		 str = "<a href = \"������ת����\"><font color=\"blue\">#" +topic.get(b)+"#</font></a>";		    		 
	    		 index=(int)right_char.get(b);
	    		 if((b+1)<topic.size()){
	    		    b++;
	    		}
	    		 	    			    	 		   	    	
	    	}
	    	
	    	else{
	    		 str = String.valueOf(temp);
	    	}
	    	
	    	new_content=new_content+str;
	    }
	    
	     System.out.println(content);
	     System.out.println(new_content);
	    
	  
	     comment.setCommentNumber(0);
	     comment.setCommentOrWeiboId(CommentId);
	     comment.setLike(0);
	     
	     Date date = new Date();
	     DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	     String time=format.format(date);
	     comment.setDate(time);
	     comment.setAtUserIdList(at);
	     comment.setTopicIdList(topic);
	     comment.setContent(new_content);
	     
	    String commentId = commentDAO.insertNewComment(comment);  //���������۲������ݿ�,�Ҵ˿���û���������۵�ID ��
	     
	    
	    commentDAO.insertCommentList(CommentId,commentId); //���� �����۵������б� ,ǰ����ԭ���ۣ������� �µ����۵�ID
	   
	    userDAO.insertCommentOnComment(commentId, CommentId); // �����۹�������
	    
	     
	     
	    /*
	     * 
	     * 
	     * ����������������Ļ���� @ ���д���
	     */
	    
	    //�Ի���Ĵ���
		  List<String>  topic_list = new ArrayList<String>();  //���е�topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //���е�topicID
			   
		    
		 //�õ����л��������
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  if(topic_name_list.contains(topic.get(i))){
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertComment(topic_list.get(index), commentId);  //�û�������� 
				  
				  System.out.println("��������⣺"+topic.get(i));
				  
			  }else{    //����һ������
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				 String topicId= topicDAO.insertNewTopic(new_topic);
				 topicDAO.insertComment(topicId, commentId);
				  
				 System.out.println("�½��˻��⣺"+topic.get(i));
			  }
		  }
		    
		  //��@�Ĵ���
		  List<String>  user_list = new ArrayList<String>();  //���е�userID
		  List<String>  user_name_list = new ArrayList<String>();  //���е�userID��name
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //����Map
		  }
		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.contains(at.get(i))){    //�����û�
				  
				  int index=user_name_list.indexOf(at.get(i));
				  messageToMeService.atMeInfromComment(user_list.get(index), commentId);   //�õ���@���û�ID  
				  
				  System.out.println("������û���"+at.get(i));
				  
			  }else{
				  
				 //�û������� ���ùܣ�
				  System.out.println("û����û���"+at.get(i));
			  }
		  }
		  
		  //�ñ����۵���֪��
		  messageToMeService.commentMyCommentInform(userId, commentId);
		  return true;
	}

}
