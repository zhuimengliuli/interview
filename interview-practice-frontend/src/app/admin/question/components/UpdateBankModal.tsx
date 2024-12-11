import { Form, message, Modal, Select } from "antd";
import React, { useEffect, useState } from "react";
import {
  addQuestionBankUsingPost,
  listQuestionBankVoByPageUsingPost,
  removeQuestionBankUsingPost,
} from "@/api/questionBankController";
import { listBankVoByPageUsingPost } from "@/api/bankController";

interface Props {
  questionId?: number;
  visible: boolean;
  onCancel: () => void;
}

/**
 * 更新题目所属题库
 * @param props
 * @constructor
 */
const UpdateBankModal: React.FC<Props> = (props) => {
  const { questionId, visible, onCancel } = props;
  const [form] = Form.useForm();
  const [bankList, setBankList] = useState<API.BankVO[]>([]);
  const getCurrentBankIdList = async () => {
    try {
      const res = await listQuestionBankVoByPageUsingPost({
        questionId,
        pageSize: 20,
      });
      const list = (res.data?.records ?? []).map((item) => item.bankId);
      console.log(list);
      form.setFieldValue("bankIdList" as any, list);
    } catch (e: any) {
      message.error("获取题目所属题库列表失败，" + e.message);
    }
  };

  useEffect(() => {
    if (questionId) {
      getCurrentBankIdList();
    }
  }, [questionId]);

  const getBankList = async () => {
    const pageSize = 200;
    try {
      const res = await listBankVoByPageUsingPost({
        pageSize,
        sortField: "createTime",
        sortOrder: "descend",
      });
      setBankList(res.data?.records ?? []);
    } catch (e: any) {
      message.error("获取题库列表失败，" + e.message);
    }
  };

  useEffect(() => {
    getBankList();
  }, []);

  return (
    <Modal
      destroyOnClose
      title={"更新所属题库"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <Form form={form} style={{ marginTop: 24 }}>
        <Form.Item label="所属题库" name="bankIdList">
          <Select
            mode="multiple"
            style={{ width: "100%" }}
            options={bankList.map((bank) => {
              return {
                label: bank.title,
                value: bank.id,
              };
            })}
            onSelect={async (value) => {
              const hide = message.loading("正在更新");
              try {
                await addQuestionBankUsingPost({
                  questionId,
                  bankId: value,
                });
                hide();
                message.success("绑定题库成功");
              } catch (error: any) {
                hide();
                message.error("绑定题库失败，" + error.message);
              }
            }}
            onDeselect={async (value) => {
              const hide = message.loading("正在更新");
              try {
                await removeQuestionBankUsingPost({
                  questionId,
                  bankId: value,
                });
                hide();
                message.success("取消绑定成功");
              } catch (error: any) {
                hide();
                message.error("取消绑定失败，" + error.message);
              }
            }}
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};
export default UpdateBankModal;
