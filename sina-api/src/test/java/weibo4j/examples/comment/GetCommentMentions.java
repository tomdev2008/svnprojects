package weibo4j.examples.comment;

import com.sina.weibo.api.Comments;
import com.sina.weibo.model.Comment;
import com.sina.weibo.model.CommentWapper;
import com.sina.weibo.model.WeiboException;

import weibo4j.examples.oauth2.Log;

public class GetCommentMentions {

	public static void main(String[] args) {
		String access_token = args[0];
		Comments cm = new Comments();
		cm.client.setToken(access_token);
		try {
			CommentWapper comment = cm.getCommentMentions();
			for (Comment c : comment.getComments()) {
				Log.logInfo(c.toString());
			}
			System.out.println(comment.getNextCursor());
			System.out.println(comment.getPreviousCursor());
			System.out.println(comment.getTotalNumber());
			System.out.println(comment.getHasvisible());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
