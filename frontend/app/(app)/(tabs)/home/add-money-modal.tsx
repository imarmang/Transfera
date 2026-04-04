import { useState } from 'react';
import { KeyboardAvoidingView, Modal, Platform, View, Text } from 'react-native';

type Props = {
  visible: boolean,
  onClose: () => void,
  onContinue: (amount: number) => void
};

export default function AddMoneyModal({ visible, onClose, onContinue}: Props) {
  const [error, setError] = useState('');
  const [amount, setAmount] = useState('');

  const QUICK_AMOUNTS = [10, 25, 50, 100, 200];

  function handlClose() {
    setAmount('');
    setError('');
    // setShowCustomInput();
    onClose();
  }

  return (
    <Modal visible={visible} animationType="slide" transparent onRequestClose={handlClose}>
      <KeyboardAvoidingView behavior={Platform.OS === 'ios'? 'padding' : undefined }>
        <Text>Hello</Text>
      </KeyboardAvoidingView>
    </Modal>
  )
}