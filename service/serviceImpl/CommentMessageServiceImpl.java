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
	
	
	// userId 被评论者的ID       comment里面的是 评论者的ID
	@Override
	public boolean CommentToWeibo(String userId, String weiboId, Comment comment) {
		// TODO Auto-generated method stub
		String content = comment.getContent();
		/*
		 * 
		 * 第一步 解析# # 和@
		 */
		List valid_topic_index  = new  ArrayList();   //合法的#
		List topic_index = new ArrayList();  //全部的#
		 
	     //先解析# #,识别所有的#，记录位置
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 //删去不合法的#
		 
		 List topic = new ArrayList();   //一个合法的话题
		 List left_char =new ArrayList();   //左#的index
		 List right_char =new ArrayList();   //右#的index
		
		 if(topic_index.isEmpty()==false){   //有话题
			 
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //两个#挨着，则删去左边 ，右边变成新的left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //一个话题被找出来了
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //方便@扫描
				   
				   
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
		
		 }
		 
		 
		
	    //扫描 @  ，遇到合法#  # ，自动跳过
	    int j=0;
	    int skip_index =-1;
	    
	    if(valid_topic_index.isEmpty()==false){       //有话题
	                skip_index = (int) valid_topic_index.get(j);
	    }else{
	    	skip_index=-1;  //不需要任何跳过
	    }
	    
	    
	    List at = new ArrayList();   //存放@的内容
	    List at_start_index = new ArrayList();  //@的开始
	    List at_end_index = new ArrayList();    //@的结束
	    
	
	    List at_index = new ArrayList();        //全部合法的@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //字符串最后一个是@  自动过滤
	    	
	    	if(index==skip_index){    //这个地方是话题,跳过整个话题
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //下一个左#
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
	    			)){                                      //解决了 @+结束  @@ @# 的问题
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    if(at_index.isEmpty()==false){   //@不能为空
	    	
	    	
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //遇到符号 需要结束
	    		at_end = index-1;
	    		
	    		if(at_start!=-1){   //没有合法@了
	    			
	    		at_end = index-1;
	    		String at_str = content.substring(at_start+1, at_end+1);
	    		at.add(at_str);
	    		at_start_index.add(at_start);
	    		at_end_index.add(at_end);
	    		
	    		k++;
	    		if(k<at_index.size()){
	    		    index = (int) at_index.get(k);
	    		    at_start=index;
		    		at_end=-1;
	    		}else{
	    			at_start = -1;
	    			at_end=-1;      //清空一对
	    		}
	    		
	    		}
	    			
	    	}
	    	//合法的字符 ，跳过
	    }
	    
	    if(k<at_index.size()){
	    //考虑到 @666 评论结束情况 就是以字符串结尾分割
	    at_start=(int) at_index.get(k);
	    at_end = content.length()-1;
	    String at_str = content.substring(at_start+1);
	    
	    at.add(at_str);
		at_start_index.add(at_start);
		at_end_index.add(at_end);
	   
	    }
	    
	    System.out.println(at.toString());
	    System.out.println(at_start_index.toString());
	    System.out.println(at_end_index.toString());
	    
	    }
	    /*
	     * 
	     * 第二步：拼接新的content， 将评论加入数据库
	     * 
	     */
	    
	    
	 
	    //
	    //拼接新的content 
	    String new_content ="";
	    
	    int a=0;  //@
	    int b=0;  //#
	    for(int index=0;index<content.length();index++){
	    	
	    	char temp = content.charAt(index);
	    	String str ="";
	    	if(at_start_index.isEmpty()==false && index==(int)at_start_index.get(a)){  //@
	    		 str = "<a href = \"at跳转动作\"><font color=\"blue\">@" +at.get(a)+"</font></a>";	    		
	    		 index =(int) at_end_index.get(a);
	    		 if((a+1)<at.size()){	    		   
	    		    a++;
	    		}
	    		 
	    	}
	    	
	    	else if(left_char.isEmpty()==false && index==(int)left_char.get(b)){   //#
	    		 str = "<a href = \"话题跳转动作\"><font color=\"blue\">#" +topic.get(b)+"#</font></a>";		    		 
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
	     
	    String commentId = commentDAO.insertNewComment(comment);  //将此条评论插入数据库,我此刻是没有这条评论的ID 的
	     
	    
	    weiboDAO.insertCommentList(weiboId, commentId); //插入 该微博的评论列表
	   
	    userDAO.insertCommentOnWeibo(commentId, weiboId); // 我评论过的微博
	     
	     
	    /*
	     * 
	     * 
	     * 第三步：对评论里的话题和 @ 进行处理
	     */
	    
	    //对话题的处理
		  List<String>  topic_list = new ArrayList<String>();  //所有的topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //所有的topicID
			   
		    
		 //得到所有话题的名字
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  
			  if(topic_name_list.isEmpty()==false){     //系统的话题不为空
			  if(topic_name_list.contains(topic.get(i))){    
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertComment(topic_list.get(index), commentId);  //该话题的评论 
				  
				  System.out.println("有这个话题并插入了评论："+topic.get(i));
				  
			  }else{    //创建一个话题
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				 String topicId= topicDAO.insertNewTopic(new_topic);
				 
				 if(topicId.equals("-1")){
				  
				    System.out.println("这个话题可能刚刚新建了，不用再新建了："+topic.get(i));
				 }else{
					 topicDAO.insertComment(topicId, commentId);	  
					 System.out.println("新建了话题并插入了评论："+topic.get(i));
				 }
			  }
			  
			  }
		  }
		    
		  //对@的处理
		  List<String>  user_list = new ArrayList<String>();  //所有的userID
		  List<String>  user_name_list = new ArrayList<String>();  //所有的userID和name
		  
		  List<String>  aleardy_at_list = new ArrayList<String>();   //已经@过的用户
		  aleardy_at_list.add("#");  //防止数组越界
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //放入Map
		  }
		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.isEmpty()==false){    //系统的用户不为空
			  if(user_name_list.contains(at.get(i)) ){    //存在用户且没有被at过
				  
				  if(aleardy_at_list.contains(at.get(i))){
					  System.out.println("已经提醒过这个用户了："+at.get(i));
				  }else{
				     int index=user_name_list.indexOf(at.get(i));
				     messageToMeService.atMeInfromComment(user_list.get(index), commentId);   //得到被@的用户ID 
				     aleardy_at_list.add((String) at.get(i));   //加入已经at了的用户名单
				  
				     System.out.println("有这个用户就提醒他消息："+at.get(i));
				  }
			  }else{
				  
				 //用户不存在 不用管？
				  System.out.println("没这个用户不用管："+at.get(i));
			  }
			  }
		  }
		  
		  //让被评论的人知道
		  messageToMeService.commentMyCommentInform(userId, commentId);
		  return true;
		
	}

	@Override
	public boolean CommentToComment(String userId, String CommentId, Comment comment) {
		// TODO Auto-generated method stub
        String content = comment.getContent();

		/*
		 * 
		 * 第一步 解析# # 和@
		 */
		List valid_topic_index  = new  ArrayList();   //合法的#
		List topic_index = new ArrayList();  //全部的#
		 
	     //先解析# #,识别所有的#，记录位置
		 for (int index= 0 ;index<content.length();index++){
			 if(content.charAt(index)=='#'){
			      topic_index.add(index);
			 }
		 }
		 
		 //删去不合法的#
		 
		 List topic = new ArrayList();   //一个合法的话题
		 List left_char =new ArrayList();   //左#的index
		 List right_char =new ArrayList();   //右#的index
		
		 if(topic_index.isEmpty()==false){   //有话题
		 
		 int left=(int) topic_index.get(0);
		 int right =0;
		 
		 for(int i=1; i<topic_index.size();i++){
			 right = (int) topic_index.get(i);
			 
			 if(right==left+1){   //两个#挨着，则删去左边 ，右边变成新的left
				 left =right;
			 }
			 
			 else if(left==-1){
				  left=(int) topic_index.get(i);
			 }
			 
			 else{    //一个话题被找出来了
				   valid_topic_index.add(left);
				   valid_topic_index.add(right);  //方便@扫描
				   
				   
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
		
	 }
		 
	    //扫描 @  ，遇到合法#  # ，自动跳过
	    int j=0;
        int skip_index =-1;
	    
	    if(valid_topic_index.isEmpty()==false){       //有话题
	                skip_index = (int) valid_topic_index.get(j);
	    }else{
	    	skip_index=-1;  //不需要任何跳过
	    }
	    
	    
	    
	    List at = new ArrayList();   //存放@的内容
	    List at_start_index = new ArrayList();  //@的开始
	    List at_end_index = new ArrayList();    //@的结束
	    
	
	    List at_index = new ArrayList();        //全部合法的@
	  
	    
	    for(int index = 0 ;index<content.length()-1;index++){     //字符串最后一个是@  自动过滤
	    	
	    	if(index==skip_index){    //这个地方是话题,跳过整个话题
	    		index=(int) valid_topic_index.get(j+1);
	    		
	    		j=j+2;  //下一个左#
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
	    			)){                                      //解决了 @+结束  @@ @# 的问题
	    		       at_index.add(index);
	    	     }
	    	}
	    }
	    
	    if(at_index.isEmpty()==false){   //@不能为空
	    	
	    
	    System.out.println(at_index.toString());
	    int k = 0;
	   
	    int at_start=(int) at_index.get(0);
	    int at_end=-1;
	    
	    for(int index=(int) at_index.get(0)+1 ;index<content.length();index++){
	    	
	    	char want = content.charAt(index);
	    	 
	    	 if(want=='@'||want=='#' || want=='!' || want==' ' || want=='$' ||
	    			want=='%' || want=='^' || want=='&' || want=='*' || want=='(' ||
	    			want==')' || want=='=' || want=='+' || want=='{' || want=='}' ) {  //遇到符号 需要结束
	    		
	    		 at_end = index-1;		
	    		if(at_start!=-1){   //没有合法@了
	    			
		    		at_end = index-1;
		    		String at_str = content.substring(at_start+1, at_end+1);
		    		at.add(at_str);
		    		at_start_index.add(at_start);
		    		at_end_index.add(at_end);
		    		
		    		k++;
		    		if(k<at_index.size()){
		    		    index = (int) at_index.get(k);
		    		    at_start=index;
			    		at_end=-1;
		    		}else{
		    			at_start = -1;
		    			at_end=-1;      //清空一对
		    		}
		    		
		    		}
	    			
	    	}
	    	//合法的字符 ，跳过
	    }
	    
	    //考虑到 @666 评论结束情况 就是以字符串结尾分割
	    if(k<at_index.size()){
	    	
	    
	    at_start=(int) at_index.get(k);
	    at_end = content.length()-1;
	    String at_str = content.substring(at_start+1);
	    
	    at.add(at_str);
		at_start_index.add(at_start);
		at_end_index.add(at_end);
	    }
	    
	    System.out.println(at.toString());
	    System.out.println(at_start_index.toString());
	    System.out.println(at_end_index.toString());
	    
	    }
	    /*
	     * 
	     * 第二步：拼接新的content， 将评论加入数据库
	     * 
	     */
	    
	    
	 
	    //
	    //拼接新的content 
	    String new_content ="";
	    
	    int a=0;  //@
	    int b=0;  //#
	    for(int index=0;index<content.length();index++){
	    	
	    	char temp = content.charAt(index);
	    	String str ="";
	    	if(at_start_index.isEmpty()==false && index==(int)at_start_index.get(a)){  //@
	    		 str = "<a href = \"at跳转动作\"><font color=\"blue\">@" +at.get(a)+"</font></a>";	    		
	    		 index =(int) at_end_index.get(a);
	    		 if((a+1)<at.size()){	    		   
	    		    a++;
	    		}
	    		 
	    	}
	    	
	    	else if(left_char.isEmpty()==false && index==(int)left_char.get(b)){   //#
	    		 str = "<a href = \"话题跳转动作\"><font color=\"#blue\">#" +topic.get(b)+"#</font></a>";		    		 
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
	     
	    String commentId = commentDAO.insertNewComment(comment);  //将此条评论插入数据库,我此刻是没有这条评论的ID 的
	     
	    
	    commentDAO.insertCommentList(CommentId,commentId); //插入 该评论的评论列表 ,前者是原评论，后者是 新的评论的ID
	   
	    userDAO.insertCommentOnComment(commentId, CommentId); // 我评论过的评论
	    
	     
	     
	    /*
	     * 
	     * 
	     * 第三步：对评论里的话题和 @ 进行处理
	     */
	    
	    //对话题的处理
		  List<String>  topic_list = new ArrayList<String>();  //所有的topicID
		  List<String>  topic_name_list = new ArrayList<String>();  //所有的topicID
			   
		    
		 //得到所有话题的名字
		  topic_list=topicDAO.getAllTopic();
		    
		  for(int i=0;i<topic_list.size();i++){
		    	String topic_name = topicDAO.getContent(topic_list.get(i));
		    	topic_name_list.add(topic_name);
		   }
		  
		  
		  for( int i=0 ;i<topic.size();i++){
			  if(topic_name_list.contains(topic.get(i))){
				  
				  int index=topic_name_list.indexOf(topic.get(i));
				  topicDAO.insertComment(topic_list.get(index), commentId);  //该话题的评论 
				  
				  System.out.println("有这个话题："+topic.get(i));
				  
			  }else{    //创建一个话题
				  Topic new_topic = new Topic();
				  
				  new_topic.setTopic((String)topic.get(i));
				  new_topic.setDate(time);
				  
				  String topicId= topicDAO.insertNewTopic(new_topic);
				  if(topicId.equals("-1")){
					  
					    System.out.println("这个话题可能刚刚新建了，不用再新建了："+topic.get(i));
				  }else{
						 topicDAO.insertComment(topicId, commentId);	  
						 System.out.println("新建了话题并插入了评论："+topic.get(i));
				  }
			  }
		  }
		    
		  //对@的处理
		  List<String>  user_list = new ArrayList<String>();  //所有的userID
		  List<String>  user_name_list = new ArrayList<String>();  //所有的userID和name
		
		  List<String>  aleardy_at_list = new ArrayList<String>();   //已经@过的用户
		  aleardy_at_list.add("#");  //防止数组越界
		  
		  user_list=userDAO.getTotalUserId();
		  
		  for(int i =0 ;i<user_list.size();i++){
			  User user = new User();
			  
			  user = userDAO.getUser(user_list.get(i));
			  user_name_list.add(user.getName());   //放入Map
		  }

		  for(int i =0 ;i<at.size();i++){
			  
			  if(user_name_list.isEmpty()==false){    //系统的用户不为空
				  if(user_name_list.contains(at.get(i)) ){    //存在用户且没有被at过
					  
					  if(aleardy_at_list.contains(at.get(i))){
						  System.out.println("已经提醒过这个用户了："+at.get(i));
					  }else{
					     int index=user_name_list.indexOf(at.get(i));
					     messageToMeService.atMeInfromComment(user_list.get(index), commentId);   //得到被@的用户ID 
					     aleardy_at_list.add((String) at.get(i));   //加入已经at了的用户名单
					  
					     System.out.println("有这个用户就提醒他消息："+at.get(i));
					  }
				  }else{
					  
					 //用户不存在 不用管？
					  System.out.println("没这个用户不用管："+at.get(i));
				  }
				  }
		  }
		  
		  //让被评论的人知道
		  messageToMeService.commentMyCommentInform(userId, commentId);
		  return true;
	}

}
