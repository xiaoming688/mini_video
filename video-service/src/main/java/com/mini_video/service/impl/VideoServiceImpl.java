package com.mini_video.service.impl;

import com.mini_video.pojo.*;
import com.mini_video.pojo.vo.CommentsVO;
import com.mini_video.pojo.vo.VideosVO;
import com.mini_video.repository.CommentRepository;
import com.mini_video.repository.UserRepository;
import com.mini_video.repository.UsersLikeVideosRepository;
import com.mini_video.repository.VideosRepository;
import com.mini_video.service.SearchRecordsService;
import com.mini_video.service.VideoService;
import com.mini_video.utils.Constants;
import com.mini_video.utils.DateUtils;
import com.mini_video.utils.PagedResult;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Service("videoService")
public class VideoServiceImpl implements VideoService {


    @Autowired
    private SearchRecordsService searchRecordsService;

    @Autowired
    private VideosRepository videosRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UsersLikeVideosRepository usersLikeVideosRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private EntityManager em;

    @Override
    @Transactional
    public Videos saveVideo(Videos video) {
        return videosRepository.save(video);
    }

    @Override
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord, Integer page, Integer pageSize) {
        // 保存热搜词
        String desc = video.getVideoDesc();
        Integer userId = video.getUserId();
        if (isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            record.setContent(desc);
            searchRecordsService.insertRecord(record);
        }


//        String sql = "select v.*,u.face_image as face_image,u.nickname as nickname " +
//                "from videos v left join users u on u.id = v.user_id where v.status=1 ";
//        if (userId != null) {
//            sql += " and v.user_id=" + userId;
//        }
//        if (desc != null) {
//            sql += " and v.video_desc like " + "'%" + desc + "%'";
//        }

//        Query query = em.createNativeQuery(sql, java.util.Map.class);

        QVideos qVideos = QVideos.videos;
        QUsers qUsers = QUsers.users;
        JPAQueryFactory query = new JPAQueryFactory(em);
        JPAQuery jpaQuery = query.select(Projections.bean(VideosVO.class,
                qVideos.audioId, qVideos.id, qVideos.userId, qVideos.videoPath.as("coverPath"),
                qVideos.videoDesc, qVideos.videoPath, qVideos.videoSeconds,
                qVideos.videoWidth, qVideos.videoHeight,
                qVideos.likeCounts, qVideos.status,
                qUsers.faceImage, qUsers.nickname))
                .from(qVideos)
                .leftJoin(qUsers)
                .on(qUsers.id.eq(qVideos.userId));
        if (userId != null) {
            jpaQuery.where(qUsers.id.eq(userId));
        }
        if (desc != null) {
            jpaQuery.where(qVideos.videoDesc.like("%" + desc + "%"));
        }
        jpaQuery.offset((page - 1) * pageSize).limit(pageSize);
        QueryResults<VideosVO> list = jpaQuery.fetchResults();
//
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list.getResults());
        pagedResult.setRecords(list.getTotal());
        pagedResult.setTotal(pageSize);

        return pagedResult;
    }


    @Override
    public PagedResult queryMyFollowVideos(String userId, Integer page, int pageSize) {

        QUsersFans qUsersFans = QUsersFans.usersFans;
        List<Integer> userIds = new JPAQuery<>(em)
                .select(qUsersFans.userId)
                .from(qUsersFans).where(qUsersFans.fanId.eq(Integer.valueOf(userId))).fetch();

        QVideos qVideos = QVideos.videos;
        QUsers qUsers = QUsers.users;
        JPAQueryFactory query = new JPAQueryFactory(em);
        JPAQuery jpaQuery = query.select(Projections.bean(VideosVO.class, qVideos.audioId,
                qVideos.videoDesc, qVideos.videoPath, qVideos.videoSeconds,
                qVideos.videoWidth, qVideos.videoHeight, qVideos.videoPath.as("coverPath"),
                qVideos.likeCounts, qVideos.status, qUsers.id.as("userId"), qUsers.faceImage, qUsers.nickname))
                .from(qVideos)
                .leftJoin(qUsers)
                .on(qUsers.id.eq(qVideos.userId))
                .where(qUsers.id.in(userIds)).where(qVideos.status.eq(1));

        jpaQuery.offset((page - 1) * pageSize).limit(pageSize);
        QueryResults<VideosVO> list = jpaQuery.fetchResults();
//
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list.getResults());
        pagedResult.setRecords(list.getTotal());
        pagedResult.setTotal(pageSize);

        return pagedResult;
    }


    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, int pageSize) {

        QUsersLikeVideos qUsersLikeVideos = QUsersLikeVideos.usersLikeVideos;
        List<Integer> videoIds = new JPAQuery<>(em)
                .select(qUsersLikeVideos.videoId)
                .from(qUsersLikeVideos).where(qUsersLikeVideos.userId.eq(Integer.valueOf(userId))).fetch();

        QVideos qVideos = QVideos.videos;
        QUsers qUsers = QUsers.users;
        JPAQuery jpaQuery = new JPAQuery<>(em).select(Projections.bean(VideosVO.class, qVideos.audioId,
                qVideos.videoDesc, qVideos.videoPath, qVideos.videoSeconds,
                qVideos.videoWidth, qVideos.videoHeight, qVideos.videoPath.as("coverPath"),
                qVideos.likeCounts, qVideos.status, qUsers.id.as("userId"), qUsers.faceImage, qUsers.nickname))
                .from(qVideos)
                .leftJoin(qUsers)
                .on(qUsers.id.eq(qVideos.userId))
                .where(qVideos.id.in(videoIds))
                .where(qVideos.status.eq(1));

        jpaQuery.offset((page - 1) * pageSize).limit(pageSize);
        QueryResults<VideosVO> list = jpaQuery.fetchResults();
//
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list.getResults());
        pagedResult.setRecords(list.getTotal());
        pagedResult.setTotal(pageSize);

        return pagedResult;
    }


    @Override
    public List<String> queryHostWords() {
        QSearchRecords qSearchRecords = QSearchRecords.searchRecords;
        return new JPAQuery<>(em).select(qSearchRecords.content).from(qSearchRecords)
                .groupBy(qSearchRecords.content).fetch();
    }

    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreaterId) {
        // 1. 保存用户和视频的喜欢点赞关联关系表
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setUserId(Integer.valueOf(userId));
        ulv.setVideoId(Integer.valueOf(videoId));
        usersLikeVideosRepository.save(ulv);

        // 2. 视频喜欢数量累加
        videosRepository.addVideoLikeCounts(Integer.valueOf(videoId));
        // 3. 用户受喜欢数量的累加
        userRepository.addReceiveLikeCounts(Integer.valueOf(videoCreaterId));
    }

    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreaterId) {
        UsersLikeVideos likeVideos = usersLikeVideosRepository.findByUserIdAndVideoId(Integer.valueOf(userId), Integer.valueOf(videoId));

        if (likeVideos != null) {
            usersLikeVideosRepository.deleteById(likeVideos.getId());
        }
        // 2. 视频喜欢数量累加
        videosRepository.reduceVideoLikeCounts(Integer.valueOf(videoId));
        // 3. 用户受喜欢数量的累加
        userRepository.reduceReceiveLikeCounts(Integer.valueOf(videoCreaterId));

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public Comments saveComment(Comments comment) {
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    @Override
    public PagedResult getAllComments(Integer videoId, Integer page, Integer pageSize) {

        QComments qComments = QComments.comments;
        QUsers qUsers = QUsers.users;

        QUsers tUser = QUsers.users;
        JPAQuery jpaQuery = new JPAQuery<>(em).select(Projections.bean(CommentsVO.class,
                qComments.id,
                qComments.videoId, qComments.comment, qComments.fromUserId, qComments.createTime,
                qUsers.faceImage, qUsers.nickname, tUser.nickname.as("toNickname")))
                .from(qComments)
                .leftJoin(qUsers)
                .on(qUsers.id.eq(qComments.fromUserId))
                .leftJoin(tUser)
                .on(tUser.id.eq(qComments.toUserId))
                .where(qComments.videoId.eq(videoId));

        jpaQuery.offset((page - 1) * pageSize).limit(pageSize);
        QueryResults<CommentsVO> list = jpaQuery.fetchResults();

        List<CommentsVO> result = list.getResults();

        Iterator<CommentsVO> it = result.iterator();
        while (it.hasNext()){
            CommentsVO data = it.next();
            data.setTimeAgoStr(DateUtils.format(data.getCreateTime()));
        }
//
        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list.getResults());
        pagedResult.setRecords(list.getTotal());
        pagedResult.setTotal(pageSize);

        return pagedResult;
    }
}
