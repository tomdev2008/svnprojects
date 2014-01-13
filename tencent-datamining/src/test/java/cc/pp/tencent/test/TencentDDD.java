package cc.pp.tencent.test;
import java.util.Map;

public class TencentDDD {
	public static class Data {
		public static class Video {
			String picurl;

			String player;

			String realurl;

			String shorturl;

			String title;

			public String getPicurl() {
				return picurl;
			}

			public String getPlayer() {
				return player;
			}

			public String getRealurl() {
				return realurl;
			}

			public String getShorturl() {
				return shorturl;
			}

			public String getTitle() {
				return title;
			}

			public void setPicurl(String picurl) {
				this.picurl = picurl;
			}

			public void setPlayer(String player) {
				this.player = player;
			}

			public void setRealurl(String realurl) {
				this.realurl = realurl;
			}

			public void setShorturl(String shorturl) {
				this.shorturl = shorturl;
			}

			public void setTitle(String title) {
				this.title = title;
			}
		}

		private Video video;

		private String city_code;

		private int count;

		private String country_code;
		private int emotiontype;
		private String emotionurl;
		private String from;
		private String fromurl;
		private String geo;
		private String head;
		private String https_head;
		private String id;
		String image;
		int isrealname;
		int isvip;
		String jing;
		String latitude;
		String location;
		String longitude;
		int mcount;
		String music;
		String name;
		String nick;
		String openid;
		String origtext;
		String province_code;
		int self;
		String source;
		int status;
		String text;
		int timestamp;
		int type;
		Map<String, String> user;
		String wei;

		public String getCity_code() {
			return city_code;
		}

		public int getCount() {
			return count;
		}

		public String getCountry_code() {
			return country_code;
		}

		public int getEmotiontype() {
			return emotiontype;
		}

		public String getEmotionurl() {
			return emotionurl;
		}

		public String getFrom() {
			return from;
		}

		public String getFromurl() {
			return fromurl;
		}

		public String getGeo() {
			return geo;
		}

		public String getHead() {
			return head;
		}

		public String getHttps_head() {
			return https_head;
		}

		public String getId() {
			return id;
		}

		public String getImage() {
			return image;
		}

		public int getIsrealname() {
			return isrealname;
		}

		public int getIsvip() {
			return isvip;
		}

		public String getJing() {
			return jing;
		}

		public String getLatitude() {
			return latitude;
		}

		public String getLocation() {
			return location;
		}

		public String getLongitude() {
			return longitude;
		}

		public int getMcount() {
			return mcount;
		}

		public String getMusic() {
			return music;
		}

		public String getName() {
			return name;
		}

		public String getNick() {
			return nick;
		}

		public String getOpenid() {
			return openid;
		}

		public String getOrigtext() {
			return origtext;
		}

		public String getProvince_code() {
			return province_code;
		}

		public int getSelf() {
			return self;
		}

		public String getSource() {
			return source;
		}

		public int getStatus() {
			return status;
		}

		public String getText() {
			return text;
		}

		public int getTimestamp() {
			return timestamp;
		}

		public int getType() {
			return type;
		}

		public Map<String, String> getUser() {
			return user;
		}

		public Video getVideo() {
			return video;
		}

		public String getWei() {
			return wei;
		}

		public void setCity_code(String city_code) {
			this.city_code = city_code;
		}

		public void setCount(int count) {
			this.count = count;
		}

		public void setCountry_code(String country_code) {
			this.country_code = country_code;
		}

		public void setEmotiontype(int emotiontype) {
			this.emotiontype = emotiontype;
		}

		public void setEmotionurl(String emotionurl) {
			this.emotionurl = emotionurl;
		}

		public void setFrom(String from) {
			this.from = from;
		}

		public void setFromurl(String fromurl) {
			this.fromurl = fromurl;
		}

		public void setGeo(String geo) {
			this.geo = geo;
		}

		public void setHead(String head) {
			this.head = head;
		}

		public void setHttps_head(String https_head) {
			this.https_head = https_head;
		}

		public void setId(String id) {
			this.id = id;
		}

		public void setImage(String image) {
			this.image = image;
		}

		public void setIsrealname(int isrealname) {
			this.isrealname = isrealname;
		}

		public void setIsvip(int isvip) {
			this.isvip = isvip;
		}

		public void setJing(String jing) {
			this.jing = jing;
		}

		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}

		public void setMcount(int mcount) {
			this.mcount = mcount;
		}

		public void setMusic(String music) {
			this.music = music;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setNick(String nick) {
			this.nick = nick;
		}

		public void setOpenid(String openid) {
			this.openid = openid;
		}

		public void setOrigtext(String origtext) {
			this.origtext = origtext;
		}

		public void setProvince_code(String province_code) {
			this.province_code = province_code;
		}

		public void setSelf(int self) {
			this.self = self;
		}

		public void setSource(String source) {
			this.source = source;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setTimestamp(int timestamp) {
			this.timestamp = timestamp;
		}

		public void setType(int type) {
			this.type = type;
		}

		public void setUser(Map<String, String> user) {
			this.user = user;
		}

		public void setVideo(Video video) {
			this.video = video;
		}

		public void setWei(String wei) {
			this.wei = wei;
		}
	}

	private Data data;

	int errcode;

	String msg;

	int ret;
	long seqid;

	public Data getData() {
		return data;
	}

	public int getErrcode() {
		return errcode;
	}

	public String getMsg() {
		return msg;
	}

	public int getRet() {
		return ret;
	}

	public long getSeqid() {
		return seqid;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public void setRet(int ret) {
		this.ret = ret;
	}

	public void setSeqid(long seqid) {
		this.seqid = seqid;
	}
}