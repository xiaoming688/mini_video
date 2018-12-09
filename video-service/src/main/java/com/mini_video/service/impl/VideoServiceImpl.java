package com.mini_video.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mini_video.pojo.QUsers;
import com.mini_video.pojo.QVideos;
import com.mini_video.pojo.SearchRecords;
import com.mini_video.pojo.Videos;
import com.mini_video.pojo.vo.VideosVO;
import com.mini_video.repository.VideosRepository;
import com.mini_video.service.SearchRecordsService;
import com.mini_video.service.VideoService;
import com.mini_video.utils.PagedResult;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Service("videoService")
public class VideoServiceImpl implements VideoService {


    @Autowired
    private SearchRecordsService searchRecordsService;

    @Autowired
    private VideosRepository videosRepository;

    @Autowired
    private EntityManager em;


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

        PageHelper.startPage(page, pageSize);

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
//        JPAQueryFactory query = new JPAQueryFactory(entityManager);
        JPAQuery jpaQuery = new JPAQuery<>(em).select(Projections.bean(VideosVO.class, qVideos.audioId,
                qVideos.videoDesc, qVideos.videoPath, qVideos.videoSeconds,
                qVideos.videoWidth, qVideos.videoHeight, qVideos.coverPath,
                qVideos.likeCounts, qVideos.status, qUsers.faceImage, qUsers.nickname))
                .from(qVideos)
                .leftJoin(qUsers)
                .on(qUsers.id.eq(qVideos.userId));
        if (userId != null) {
            jpaQuery.where(qUsers.id.eq(userId));
        }
        if (desc != null) {
            jpaQuery.where(qVideos.videoDesc.like("%"+desc+"%"));
        }
        List<VideosVO> list = jpaQuery.fetch();

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }
}
