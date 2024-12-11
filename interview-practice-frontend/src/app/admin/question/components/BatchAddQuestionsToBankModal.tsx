import { Button, Form, message, Modal, Select } from "antd";
import React, { useEffect, useState } from "react";
import { batchAddQuestionsToBankUsingPost } from "@/api/questionBankController";
import { listBankVoByPageUsingPost } from "@/api/bankController";

interface Props {
  questionIdList?: number[];
  visible: boolean;
  onSubmit: () => void;
  onCancel: () => void;
}

/**
 * 批量添加题目到题库弹窗
 * @param props
 * @constructor
 */
const BatchAddQuestionsToBankModal: React.FC<Props> = (props) => {
  const { questionIdList, visible, onCancel, onSubmit } = props;
  const [form] = Form.useForm();
  const [bankList, setBankList] = useState<API.BankVO[]>([]);

  /**
   * 提交
   *
   * @param values
   */
  const doSubmit = async (values: API.QuestionBankBatchAddRequest) => {
    const hide = message.loading("正在添加");
    const bankId = values.bankId;
    if (!bankId) {
      message.error("请选择题库");
      return;
    }
    try {
      await batchAddQuestionsToBankUsingPost({
        questionIdList,
        bankId,
      });
      hide();
      message.success("添加成功");
      onSubmit?.();
    } catch (error: any) {
      hide();
      message.error("添加失败，" + error.message);
    }
  };

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
      title={"批量添加题目到题库"}
      open={visible}
      footer={null}
      onCancel={() => {
        onCancel?.();
      }}
    >
      <Form form={form} style={{ marginTop: 24 }} onFinish={doSubmit}>
        <Form.Item label="选择题库" name="bankId">
          <Select
            style={{ width: "100%" }}
            options={bankList.map((bank) => {
              return {
                label: bank.title,
                value: bank.id,
              };
            })}
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit">
            提交
          </Button>
        </Form.Item>
      </Form>
    </Modal>
  );
};
export default BatchAddQuestionsToBankModal;
