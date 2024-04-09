package com.easybbs.entity.enums;

public enum ArticleAttachmentTypeEnum {
        NO_ATTACHMENT(0,"没有附件"),
        HAVE_ATTACHMENT(1,"有附件");
        private Integer type;
        private String desc;

    ArticleAttachmentTypeEnum(Integer type,String desc) {
            this.type = type;
            this.desc = desc;
        }

        public Integer getType() {
            return type;
        }


        public String getDesc() {
            return desc;
        }

        public static com.easybbs.entity.enums.AttachmentFileTypeEnum getByType(Integer type){
            for (com.easybbs.entity.enums.AttachmentFileTypeEnum item: com.easybbs.entity.enums.AttachmentFileTypeEnum.values()){
                if (item.getType().equals(type)) {
                    return item;
                }
            }
            return null;
        }
}
