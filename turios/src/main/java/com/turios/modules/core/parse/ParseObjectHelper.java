package com.turios.modules.core.parse;

public class ParseObjectHelper {

	public static final String objectId = "objectId";

	// public static final String createdAt = "createdAt";
	// public static final String updatedAt = "updatedAt";
	// public static final String ACL = "ACL";

	public static class User {
		public static final String objectname = "User";
		public static final String username = "username";
		public static final String password = "password";
	}

	public static class ParseInstallation {
		public static final String objectname = "ParseInstallation";
		public static final String name = "name";
		public static final String owner = "owner";
		public static final String turnouts = "turnouts";
		public static final String profile = Profile.objectname;
		public static final String modulespaid = ModulesPaid.objectname;
		public static final String sendChannelRelation = ChannelSendRelation.objectname;
		public static final String listenChannelRelation = ChannelListenRelation.objectname;
	}

	public static class Profile {
		public static final String objectname = "Profile";
		public static final String name = "name";
		public static final String owner = "owner";
	}

	public static class ModulesPaid {
		public static final String objectname = "ModulesPaid";
	}

	public static class Channel {
		public static final String objectname = "Channel";
		public static final String name = "name";
		public static final String owner = "owner";
	}

	public static class ChannelListenRelation {
		public static final String objectname = "ChannelListenRelation";
	}

	public static class ChannelSendRelation {
		public static final String objectname = "ChannelSendRelation";
	}
}
